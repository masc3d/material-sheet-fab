package org.deku.leoz.central.service.internal

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR_ENTRY
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourEntryRecord
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.TourServiceV1.*
import org.deku.leoz.node.service.internal.SmartlaneBridge
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.id
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.zalando.problem.Status
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.rs.RestProblem
import sx.time.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink
import kotlin.NoSuchElementException

/**
 * Tour service implementation
 * Created by masc on 14.12.17.
 */
@Named
@Path("internal/v1/tour")
class TourServiceV1
    :
        org.deku.leoz.service.internal.TourServiceV1,
        MqHandler<Any> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    //region Dependencices
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    // Repositories
    @Inject
    private lateinit var deliverylistRepository: JooqDeliveryListRepository
    @Inject
    private lateinit var tourRepository: JooqTourRepository
    @Inject
    private lateinit var userRepository: JooqUserRepository
    @Inject
    private lateinit var nodeRepository: JooqNodeRepository

    @Inject
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var smartlane: SmartlaneBridge
    //endregion

    /**
     * Create new tours from domain instance(s)
     * @param tours Tours to create
     * @return Ids of created tours
     */
    fun create(tours: Iterable<Tour>): List<Int> {
        return dsl.transactionResult { _ ->
            tours.map { tour ->
                // Create new one if it doesn't exist
                val tourRecord = dsl.newRecord(TAD_TOUR).also {
                    it.userId = tour.userId
                    it.stationNo = tour.stationNo
                    it.deliverylistId = tour.deliverylistId
                    it.optimized = tour.optimized?.toTimestamp()
                    it.store()
                }

                tour.stops.forEachIndexed { index, stop ->
                    stop.tasks.forEach { task ->
                        dsl.newRecord(TAD_TOUR_ENTRY).also {
                            it.tourId = tourRecord.id
                            it.position = (index + 1).toDouble()
                            it.orderId = task.orderId
                            it.orderTaskType = when (task.taskType) {
                                Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                Task.Type.PICKUP -> TaskType.DELIVERY.value
                            }
                            it.store()
                        }
                    }
                }

                tourRecord.id
            }
        }
    }

    /**
     * Update existing tour from optimized one
     * @param tour Optimized tour
     */
    fun updateFromOptimizedTour(tour: Tour) {
        dsl.transaction { _ ->
            val tourId = tour.id ?: throw IllegalArgumentException("Tour id cannot be null")

            val now = Date().toTimestamp()
            val entryRecords = tourRepository.findEntriesById(tourId)

            tour.stops
                    .forEachIndexed { index, stop ->
                        // Select stop tour entry which matches custom id
                        val entryRecord = entryRecords.first { it.id == stop.id }

                        val newPosition = (index + 1).toDouble()
                        log.trace { "OPTIMIZATION POSITION UPDATE ${entryRecord.position} -> ${newPosition}" }

                        // Update position for all entries on the same (positional) level
                        val entryIds = entryRecords
                                .filter { it.position == entryRecord.position }
                                .map { it.id }

                        dsl.update(TAD_TOUR_ENTRY)
                                .set(TAD_TOUR_ENTRY.POSITION, newPosition)
                                .set(TAD_TOUR_ENTRY.TIMESTAMP, now)
                                .where(TAD_TOUR_ENTRY.ID.`in`(entryIds))
                                .execute()
                    }

            dsl.update(TAD_TOUR)
                    .set(TAD_TOUR.OPTIMIZED, now)
                    .where(TAD_TOUR.ID.eq(tourId))
                    .execute()
        }
    }

    //region REST
    /**
     * Get tours
     */
    override fun get(
            debitorId: Int?,
            stationNo: Int?,
            userId: Int?
    ): List<Tour> {
        val tourRecords = dsl
                .selectFrom(TAD_TOUR)
                .where()
                .let {
                    when {
                        debitorId != null -> it.and(TAD_TOUR.USER_ID.`in`(
                                userRepository.findUserIdsByDebitor(debitorId)
                        ))
                        else -> it
                    }
                }
                .let {
                    when {
                        stationNo != null -> it.and(TAD_TOUR.STATION_NO.eq(stationNo))
                        else -> it
                    }
                }
                .let {
                    when {
                        userId != null -> it.and(TAD_TOUR.USER_ID.eq(userId))
                        else -> it
                    }
                }
                .fetchArray()
                .also {
                    if (it.size == 0) {
                        // No tour records in selection
                        throw NoSuchElementException()
                    }
                }

        // Pre-fetch relevant records
        val nodeUidsById = dsl.select(MST_NODE.NODE_ID, MST_NODE.KEY)
                .from(MST_NODE)
                .where(MST_NODE.NODE_ID.`in`(
                        tourRecords.mapNotNull { it.nodeId }
                ))
                .associate { Pair(it.value1(), it.value2()) }

        val tourEntriesByTourId = tourRepository
                .findEntriesByIds(tourRecords.map { it.id })
                .groupBy { it.tourId }

        return tourRecords.map {
            it.toTour(
                    nodeUid = it.nodeId?.let { nodeId -> nodeUidsById.getValue(nodeId) },
                    tourEntryRecordsByTourId = tourEntriesByTourId
            )
        }
    }

    /**
     * Get tour by id
     */
    override fun getById(id: Int): Tour {
        val tourRecord = tourRepository.findById(id)
                ?: throw NoSuchElementException("No tour with id [${id}]")

        val nodeUid = tourRecord.nodeId?.let {
            dsl.selectFrom(MST_NODE)
                    .fetchUidById(tourRecord.nodeId)
                    ?: throw NoSuchElementException("No node uid for id [${tourRecord.nodeId}]")
        }

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get tour by node
     */
    override fun getByNode(nodeUid: String): Tour {
        val nodeId = this.nodeRepository.findByKeyStartingWith(nodeUid)?.nodeId
                ?: throw NoSuchElementException("Unknown node uid ${nodeUid}")

        val tourRecord = tourRepository.findByNodeId(nodeId)
                ?: throw NoSuchElementException()

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get the (current) tour for a user
     */
    override fun getByUser(userId: Int): Tour {
        val tourRecord = tourRepository.findLatestByUserId(userId)
                ?: throw NoSuchElementException("No assignable tour for user [${userId}]")

        return tourRecord.toTour()
    }

    override fun delete(ids: List<Int>) {
        dsl.transaction { _ ->
            ids.forEach {
                this.tourRepository.delete(it)
            }
        }
    }

    /**
     * Optimize tours
     */
    override fun optimize(
            ids: List<Int>,
            waitForCompletion: Boolean,
            options: TourOptimizationOptions,
            response: AsyncResponse) {

        data class Optimization(val tourId: Int, val result: List<Tour>)

        try {
            log.info("Starting ruote optimization for tour(s) [${ids.joinToString(", ")}] ")

            // Schedule multiple optimizations
            Observable.mergeDelayError(ids.map { id ->
                val tour = this.getById(id)

                if (tour.nodeUid != null) {
                    // Node restrictions
                    if (options.vehicles?.count() ?: 0 == 0)
                        throw IllegalStateException("In place updates (vehicles not provided) are not supported for tours owned by nodes")
                }

                this.optimize(
                        tour = tour,
                        options = options
                )
                        .toObservable()
                        .map { Optimization(tourId = id, result = it) }
            })
                    .subscribeBy(
                            onNext = { optimization ->
                                val tours = optimization.result

                                log.info("Ruote optimization completed for tour [${optimization.tourId}]")

                                if (options.vehicles?.count() ?: 0 > 0) {
                                    // Create tours from optimized results
                                    this.create(tours)
                                } else {
                                    this.updateFromOptimizedTour(tours.first())
                                }
                            },
                            onComplete = {
                                log.info("Route optimization process completed")

                                if (waitForCompletion) {
                                    response.resume(Response
                                            .status(Response.Status.OK)
                                            .build()
                                    )
                                }
                            },
                            onError = { e ->
                                log.error(e.message, e)

                                if (waitForCompletion) {
                                    response.resume(RestProblem(
                                            status = Status.INTERNAL_SERVER_ERROR,
                                            detail = e.message
                                    ))
                                }
                            }
                    )


            if (!waitForCompletion) {
                response.resume(Response
                        .status(Response.Status.ACCEPTED)
                        .build()
                )
            }
        } catch (e: Exception) {
            throw RestProblem(
                    status = Status.INTERNAL_SERVER_ERROR,
                    detail = e.message
            )
        }
    }

    /**
     * Optimize tour
     * @param id Tour id
     * @param response Asynchronous response
     */
    override fun optimize(
            id: Int,
            waitForCompletion: Boolean,
            options: TourOptimizationOptions,
            response: AsyncResponse) {

        this.optimize(
                ids = listOf(id),
                waitForCompletion = waitForCompletion,
                options = options,
                response = response)
    }

    override fun status(stationNo: Int, sink: SseEventSink, sse: Sse) {
        var subscription: Disposable? = null

        //this.stationRepository.findById(stationId)

        val tourIds = dsl.selectFrom(TAD_TOUR)
                .where(TAD_TOUR.STATION_NO.eq(stationNo))
                .fetch(TAD_TOUR.ID)

        // The status request uuid
        val uuid = "${UUID.randomUUID()}"

        /** Helper to close down (with optional error) */
        fun close(error: Throwable? = null) {
            subscription?.dispose()
            sink.close()

            log.info { "SSE CLOSED [${uuid}] ${error?.message ?: ""}" }
        }

        subscription = Observable.merge(
                // Emit interval based (ping) event in order to detect remote disconnection
                Observable.interval(1, TimeUnit.MINUTES)
                        .map { sse.newEventBuilder().id(uuid).build() }
                ,
                // Listen for updates
                this.optimizations.updated
                        .filter { tourIds.contains(it.id) }
                        .doOnSubscribe {
                            log.trace { "SSE SUBSCRIBED [${uuid}]" }
                        }
                        .doOnComplete {
                            log.trace { "SSE COMPLETED [${uuid}]" }
                        }
                        .doOnDispose {
                            log.trace { "SSE DISPOSED [${uuid}]" }
                        }
                        .map { status ->
                            // Build SSE event from status update
                            sse.newEventBuilder()
                                    .id(uuid)
                                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                                    .data(status.javaClass, status)
                                    .build()
                        }
        )
                .takeUntil { sink.isClosed }
                .subscribe { event ->
                    try {
                        // Send SSE event for each update
                        sink.send(event)
                                .whenComplete { result, e ->
                                    val error: Throwable? = when {
                                        result is Throwable -> result
                                        e != null -> e
                                        else -> null
                                    }

                                    if (error != null)
                                        close(error)
                                }
                                .exceptionally { e ->
                                    close(e)
                                    null
                                }
                    } catch (e: Throwable) {
                        close(e)
                    }
                }
    }

    override fun optimizeForNode(
            nodeUid: String,
            options: TourOptimizationOptions
    ) {
        try {
            this.optimizeForNode(
                    nodeUid = nodeUid,
                    nodeRequestUid = null,
                    options = options
            )
        } catch (e: Exception) {
            throw RestProblem(
                    status = Status.INTERNAL_SERVER_ERROR,
                    detail = e.message
            )
        }
    }

    /**
     * Start optimization for a node tour (asynchronously)
     * @param nodeUid Node uid
     * @param nodeRequestUid Message based request uid
     * @param options Optimization options
     * @throws NoSuchElementException
     * @throws IllegalArgumentException
     */
    fun optimizeForNode(
            nodeUid: String,
            nodeRequestUid: String? = null,
            options: TourOptimizationOptions
    ) {
        val nodeRecord = dsl.selectFrom(MST_NODE)
                .fetchByUid(nodeUid, strict = false)
                ?: throw NoSuchElementException("Node not found")

        if (options.vehicles?.count() ?: 0 > 1)
            throw IllegalArgumentException("Multiple vehicles are not supported when " +
                    "optimizing a single (node related) tour")

        val tourRecord = tourRepository.findByNodeId(nodeRecord.nodeId)
                ?: throw NoSuchElementException("No tour for this node")

        val tour = this.getById(tourRecord.id)

        /** Handle error response on message based requests (nodeRequestUid was provided) */
        fun handleErrorResponse(error: TourOptimizationResult.ErrorType) {
            // Only send error response on node requests
            if (nodeRequestUid != null) {
                JmsEndpoints.node.topic(identityUid = Identity.Uid(nodeRecord.uid))
                        .channel()
                        .send(TourOptimizationResult(
                                requestUid = nodeRequestUid,
                                nodeUid = nodeRecord.uid,
                                error = error
                        ))
            }
        }

        try {
            this.optimize(
                    tour = tour,
                    options = options
            )
                    .subscribeBy(
                            onSuccess = { tours ->
                                JmsEndpoints.node.topic(identityUid = Identity.Uid(nodeRecord.uid))
                                        .channel()
                                        .send(TourOptimizationResult(
                                                requestUid = nodeRequestUid,
                                                nodeUid = nodeRecord.uid,
                                                tour = tours.first()
                                        ))
                            },
                            onError = { e ->
                                log.error(e.message, e)

                                handleErrorResponse(TourOptimizationResult.ErrorType
                                        .ROUTE_COULD_NOT_BE_DETERMINED)
                            }
                    )
        } catch (e: Throwable) {
            handleErrorResponse(TourOptimizationResult.ErrorType
                    .REMOTE_REQUEST_FAILED)

            throw e
        }
    }

    override fun create(deliverylistIds: List<Int>): List<Tour> {
        val dlRecords = deliverylistRepository.findByIds(
                deliverylistIds.map { it.toLong() }
        )

        dlRecords.map { it.id.toInt() }.let { deliverylistIds.subtract(it) }.also { missing ->
            if (missing.count() > 0)
                throw RestProblem(
                        status = Status.NOT_FOUND,
                        detail = "One or more delivery lists could not be found [${missing.joinToString(", ")}]")
        }

        val dlDetailRecordsById = deliverylistRepository.findDetailsByIds(
                deliverylistIds.map { it.toLong() }
        )
                .groupBy { it.id }

        val timestamp = Date().toTimestamp()

        // Create tour/entries from delivery list
        val tours = dsl.transactionResult { _ ->
            dlRecords.map { dlRecord ->
                // Create new tour
                val tourRecord = dsl.newRecord(TAD_TOUR).also {
                    it.nodeId = null
                    it.userId = null
                    it.stationNo = dlRecord.deliveryStation.toInt()
                    it.deliverylistId = dlRecord.id.toInt()
                    it.timestamp = timestamp
                    it.store()
                }

                val dlDetailRecords = dlDetailRecordsById.getValue(dlRecord.id)

                // Transform delivery list detail to tour entry record
                val tourEntryRecords = dlDetailRecords.mapIndexed { index, dlDetailRecord ->
                    dsl.newRecord(TAD_TOUR_ENTRY).also {
                        it.tourId = tourRecord.id
                        it.orderId = dlDetailRecord.orderId.toLong()
                        it.orderTaskType = TaskType.valueOf(dlDetailRecord.stoptype).value
                        it.position = (index + 1).toDouble()
                        it.timestamp = timestamp
                        it.store()
                    }
                }

                // TODO: optimize performance: using iterable conversion extension
                tourRecord.toTour(
                        tourEntryRecordsByTourId = mapOf(Pair(tourRecord.id, tourEntryRecords))
                )
            }
        }

        return tours.toList()
    }
    //endregion

    //region Optimization
    /**
     * Tracks optimizations and provides notifications
     */
    inner class Optimizations : Iterable<TourOptimizationStatus> {
        /** Holds all current optimizations (by tour id) */
        private val byId = ConcurrentHashMap<Int, TourOptimizationStatus>()

        private val updatedSubject = PublishSubject.create<TourOptimizationStatus>()
        /** Update notifications */
        val updated = Observable
                // Emit all current statuses initially
                .fromIterable(this)
                // ..followed by live updates
                .concatWith(updatedSubject.hide())

        /**
         * Should be called when tour optimization is started
         * @param tourRecord Tour record
         */
        fun onStart(id: Int) {
            TourOptimizationStatus(id = id, inProgress = true).also { status ->
                this.byId.set(id, status)
                this.updatedSubject.onNext(status)
            }
        }

        /**
         * Should be called when tour optimization finishes
         * @param tourRecord Tour record
         */
        fun onFinish(id: Int) {
            // TODO: emit errors
            TourOptimizationStatus(id = id, inProgress = false).also { status ->
                this.byId.remove(id)
                this.updatedSubject.onNext(status)
            }
        }

        override fun iterator(): Iterator<TourServiceV1.TourOptimizationStatus> = this.byId.values.iterator()

        init {
            this.updated
                    .subscribe {
                        log.trace { "UPDATED ${it}" }
                    }
        }
    }

    /**
     * Pending optimizations
     */
    val optimizations = Optimizations()

    /**
     * Optimize a tour
     * @param tour Tour to optimize
     * @param options Optimization options
     * @return Single observable of optimized tours
     */
    fun optimize(
            tour: Tour,
            options: TourOptimizationOptions
    ): Single<List<Tour>> {
        // Check if optimization for this tour is already in progress
        if (this.optimizations.any { it.id == tour.id })
            throw IllegalStateException("Optimization for tour [${tour.id}] already in progress")

        val tourId = tour.id!!

        return this.smartlane.optimize(
                tour = tour,
                options = options
        )
                .doOnSubscribe {
                    this.optimizations.onStart(tourId)
                }
                .doFinally {
                    this.optimizations.onFinish(tourId)
                }
    }
    //endregion

    //region Transformations
    private fun Iterable<TadTourRecord>.toTours(): List<Tour> {
        TODO("not implemented")
    }

    /**
     * Transform tour/node record into service entity
     * @param nodeUid OPTIONAL node record referring to this tour. If not provided it will be looked up.
     * @param tourEntryRecordsByTourId OPTIONAL tour entry record map for fast lookups
     */
    private fun TadTourRecord.toTour(
            nodeUid: String? = null,
            tourEntryRecordsByTourId: Map<Int, List<TadTourEntryRecord>>? = null): Tour {
        val tourRecord = this

        @Suppress("NAME_SHADOWING")
        val nodeUid = this.nodeId?.let {
            nodeUid ?: dsl.select(MST_NODE.KEY)
                    .from(MST_NODE)
                    .where(MST_NODE.NODE_ID.eq(tourRecord.nodeId))
                    .fetchOne().value1()
        }

        // Fetch tour entries. Equal positions represent stop tasks in order of PK
        val tourEntryRecords = if (tourEntryRecordsByTourId != null)
            tourEntryRecordsByTourId.get(tourRecord.id) ?: listOf()
        else
            tourRepository.findEntriesById(tourRecord.id)

        val orders = this@TourServiceV1.orderService.getByIds(
                tourEntryRecords.map { it.orderId }.distinct()
        )

        val ordersById = mapOf(
                *orders.map { Pair(it.id, it) }.toTypedArray()
        )

        return Tour(
                id = tourRecord.id,
                nodeUid = nodeUid,
                userId = tourRecord.userId,
                stationNo = tourRecord.stationNo,
                deliverylistId = tourRecord.deliverylistId,
                optimized = tourRecord.optimized,
                orders = orders,
                stops = tourEntryRecords.groupBy { it.position }
                        .map { stop ->
                            val tasks = stop.value.map { task ->
                                val taskType = TaskType.valueMap.getValue(task.orderTaskType)
                                val order = ordersById.getValue(task.orderId)

                                Task(
                                        id = task.id,
                                        orderId = task.orderId,
                                        appointmentStart = when (taskType) {
                                            TaskType.DELIVERY -> order.deliveryAppointment.dateStart
                                            TaskType.PICKUP -> order.pickupAppointment.dateStart
                                        },
                                        appointmentEnd = when (taskType) {
                                            TaskType.DELIVERY -> order.deliveryAppointment.dateEnd
                                            TaskType.PICKUP -> order.pickupAppointment.dateEnd
                                        },
                                        taskType = when (taskType) {
                                            TaskType.DELIVERY -> Task.Type.DELIVERY
                                            TaskType.PICKUP -> Task.Type.PICKUP
                                        }
                                )
                            }

                            Stop(
                                    address = stop.value.first().let { task ->
                                        orders.first { it.id == task.orderId }.let {
                                            when (TaskType.valueMap.getValue(task.orderTaskType)) {
                                                TaskType.DELIVERY -> it.deliveryAddress
                                                TaskType.PICKUP -> it.pickupAddress
                                            }
                                        }
                                    },
                                    tasks = tasks,
                                    appointmentStart = tasks.map { it.appointmentStart }.filterNotNull().max(),
                                    appointmentEnd = tasks.map { it.appointmentEnd }.filterNotNull().min(),
                                    weight = tasks
                                            .map { ordersById.getValue(it.orderId) }
                                            .flatMap { it.parcels }
                                            .sumByDouble { it.dimension.weight }
                            )
                        }
        )
    }
    //endregion

    //region MQ handlers
    @MqHandler.Types(
            TourUpdate::class,
            TourOptimizationRequest::class
    )
    override fun onMessage(message: Any, replyChannel: MqChannel?) {
        when (message) {
            is TourUpdate -> {
                this.onMessage(message)
            }
            is TourOptimizationRequest -> {
                this.onMessage(message)
            }
        }
    }

    /**
     * Tour optimization request message handler
     */
    private fun onMessage(message: TourOptimizationRequest) {
        val nodeUid = message.nodeUid ?: run {
            log.warn("Tour optimization request received without node uid")
            return
        }

        try {
            this.optimizeForNode(
                    nodeUid = nodeUid,
                    nodeRequestUid = message.requestUid,
                    options = message.options
            )
        } catch (e: Throwable) {
            log.error(e.message)
        }
    }

    /**
     * Tour update message handler
     */
    private fun onMessage(message: TourUpdate) {
        log.trace { "Tour message ${message}" }

        val tour = message.tour ?: run {
            return
        }

        val nodeUid = tour.nodeUid ?: run {
            log.trace("Updates without node uid are not supported (yet)")
            return
        }

        val nodeId = dsl.selectFrom(MST_NODE).fetchByUid(nodeUid)
                ?.nodeId
                ?: run {
                    log.warn("Node ${nodeUid} doesn't exist. Discarding message")
                    return
                }

        // Upsert stop list
        dsl.transaction { _ ->
            // Check for existing tour
            val tourRecord = dsl.fetchOne(
                    TAD_TOUR,
                    TAD_TOUR.NODE_ID.eq(nodeId)
            ) ?:
            // Create new one if it doesn't exist
            dsl.newRecord(TAD_TOUR).also {
                it.userId = tour.userId
                it.nodeId = nodeId
                it.timestamp = message.timestamp.toTimestamp()
                it.store()
            }

            // Recreate tour entries from update
            dsl.delete(TAD_TOUR_ENTRY)
                    .where(TAD_TOUR_ENTRY.TOUR_ID.eq(tourRecord.id))
                    .execute()

            tour.stops
                    .forEachIndexed { index, stop ->
                        stop.tasks.forEach { task ->
                            dsl.newRecord(TAD_TOUR_ENTRY).also {
                                it.tourId = tourRecord.id
                                it.position = (index + 1).toDouble()
                                it.orderId = task.orderId
                                it.orderTaskType = when (task.taskType) {
                                    Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                    Task.Type.PICKUP -> TaskType.DELIVERY.value
                                }
                                it.store()
                            }
                        }
                    }
        }
    }
    //endregion
}
