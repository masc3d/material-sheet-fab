package org.deku.leoz.central.service.internal

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPADeleteClause
import com.querydsl.jpa.impl.JPAQuery
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TaskType
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.QTadTourEntry.tadTourEntry
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.deku.leoz.node.data.repository.TadTourEntryRepository
import org.deku.leoz.node.data.repository.TadTourRepository
import org.deku.leoz.node.data.jpa.transaction
import org.deku.leoz.node.service.internal.SmartlaneBridge
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.TourServiceV1.*
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
import sx.time.toTimestamp
import sx.util.toNullable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
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

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    // Repositories
    @Inject
    private lateinit var deliverylistRepository: JooqDeliveryListRepository
    @Inject
    private lateinit var userRepository: JooqUserRepository
    @Inject
    private lateinit var nodeRepository: JooqNodeRepository

    @Inject
    private lateinit var tourRepo: TadTourRepository
    @Inject
    private lateinit var tourEntryRepo: TadTourEntryRepository

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
    fun create(tours: Iterable<Tour>): List<Long> {
        val em = this.entityManagerFactory.createEntityManager()

        val now = Date()

        return em.transaction {
            tours.map { tour ->
                // Create new one if it doesn't exist
                val tourRecord = TadTour().also {
                    it.userId = tour.userId
                    it.stationNo = tour.stationNo
                    it.deliverylistId = tour.deliverylistId
                    it.optimized = tour.optimized?.toTimestamp()
                    it.uid = UUID.randomUUID().toString()
                    it.timestamp = now.toTimestamp()

                    em.persist(it)
                    em.flush()
                }

                tour.stops.forEachIndexed { index, stop ->
                    stop.tasks.forEach { task ->

                        TadTourEntry().also {
                            it.tourId = tourRecord.id
                            it.position = (index + 1).toDouble()
                            it.orderId = task.orderId
                            it.orderTaskType = when (task.taskType) {
                                Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                Task.Type.PICKUP -> TaskType.DELIVERY.value
                            }
                            it.uid = UUID.randomUUID().toString()
                            it.timestamp = now.toTimestamp()

                            em.persist(it)
                        }
                    }
                }

                tourRecord.id.toLong()
            }
        }
    }

    /**
     * Update existing tour from optimized one
     * @param tour Optimized tour
     */
    fun updateFromOptimizedTour(tour: Tour) {
        val em = this.entityManagerFactory.createEntityManager()

        em.transaction {
            val tourId = tour.id ?: throw IllegalArgumentException("Tour id cannot be null")

            val now = Date().toTimestamp()
            val entryRecords = tourEntryRepo.findAll(tadTourEntry.tourId.eq(tourId))

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

                        // TODO debug (modification of entry records must not modify iterated results)

                        tourEntryRepo.findAll(tadTourEntry.id.`in`(entryIds))
                                .forEach {
                                    it.position
                                    it.timestamp = now

                                    em.merge(it)
                                }
                    }

            tourRepo.findById(tourId).get().also {
                it.optimized = now

                em.merge(it)
            }
        }
    }

    //region REST
    /**
     * Get tours
     */
    override fun get(
            debitorId: Long?,
            stationNo: Long?,
            userId: Long?
    ): List<Tour> {
        val em = this.entityManagerFactory.createEntityManager()

        val tourRecords =
                this.tourRepo.findAll(BooleanBuilder()
                        .let {
                            when {
                                debitorId != null -> it.and(tadTour.userId.`in`(
                                        userRepository.findUserIdsByDebitor(debitorId.toInt()).map { it.toLong() }
                                ))
                                else -> it
                            }
                        }
                        .let {
                            when {
                                stationNo != null -> it.and(tadTour.stationNo.eq(stationNo))
                                else -> it
                            }
                        }
                        .let {
                            when {
                                userId != null -> it.and(tadTour.userId.eq(userId))
                                else -> it
                            }
                        }
                )
                        .also {
                            if (it.count() == 0) {
                                // No tour records in selection
                                throw NoSuchElementException()
                            }
                        }

        // Pre-fetch relevant records
        val nodeUidsById =
                dsl.select(MST_NODE.NODE_ID, MST_NODE.KEY)
                        .from(MST_NODE)
                        .where(MST_NODE.NODE_ID.`in`(
                                tourRecords.mapNotNull { it.nodeId }
                        ))
                        .associate { Pair(it.value1().toLong(), it.value2()) }

        val tourEntries =
                tourEntryRepo
                        .findAll(tadTourEntry.tourId.`in`(tourRecords.map { it.id }))

        val orders =
                this.orderService.getByIds(tourEntries
                        .map { it.orderId }
                        .distinct())

        return tourRecords.map {
            it.toTour(
                    nodeUid = it.nodeId?.let { nodeId -> nodeUidsById.getValue(nodeId) },
                    orderRecordsById = orders.associateBy { it.id },
                    tourEntryRecordsByTourId = tourEntries.groupBy { it.tourId }
            )
        }
    }

    /**
     * Get tour by id
     */
    override fun getById(id: Long): Tour {
        val tourRecord = tourRepo.findById(id)
                .toNullable()
                ?: throw NoSuchElementException("No tour with id [${id}]")

        val nodeUid = tourRecord.nodeId?.let {
            dsl.selectFrom(MST_NODE)
                    .fetchUidById(tourRecord.nodeId.toInt())
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

        val tourRecord = tourRepo
                .findOne(tadTour.nodeId.eq(nodeId.toLong()))
                .toNullable()
                ?: throw NoSuchElementException()

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get the (current) tour for a user
     */
    override fun getByUser(userId: Long): Tour {
        val em = this.entityManagerFactory.createEntityManager()

        // Get latest tour for user
        val tourRecord = JPAQuery<TadTour>(em)
                .from(tadTour)
                .where(tadTour.userId.eq(userId))
                .orderBy(tadTour.timestamp.desc())
                .fetchFirst()
                ?: throw NoSuchElementException("No assignable tour for user [${userId}]")

        return tourRecord.toTour()
    }

    override fun delete(
            ids: List<Long>,
            userId: Long?,
            stationNo: Long?) {

        val em = this.entityManagerFactory.createEntityManager()

        em.transaction {
            ids
                    .plus(stationNo?.let {
                        JPAQuery<TadTour>(em).from(tadTour)
                                .select(tadTour.id)
                                .where(tadTour.stationNo.eq(it))
                                .fetch()
                    } ?: listOf())
                    .plus(userId?.let {
                        JPAQuery<TadTour>(em).from(tadTour)
                                .select(tadTour.id)
                                .where(tadTour.userId.eq(it))
                                .fetch()
                    } ?: listOf())
                    .also {
                        if (it.count() > 0) {
                            val tourIds = it.distinct()

                            JPADeleteClause(em, tadTourEntry)
                                    .where(tadTourEntry.tourId.`in`(tourIds))
                                    .execute()

                            JPADeleteClause(em, tadTour)
                                    .where(tadTour.id.`in`(tourIds))
                                    .execute()
                        }
                    }
        }
    }

    /**
     * Optimize tours
     */
    override fun optimize(
            ids: List<Long>,
            waitForCompletion: Boolean,
            options: TourOptimizationOptions,
            response: AsyncResponse) {

        data class Optimization(val tourId: Long, val result: List<Tour>)

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
            id: Long,
            waitForCompletion: Boolean,
            options: TourOptimizationOptions,
            response: AsyncResponse) {

        this.optimize(
                ids = listOf(id),
                waitForCompletion = waitForCompletion,
                options = options,
                response = response)
    }

    override fun status(stationNo: Long, sink: SseEventSink, sse: Sse) {
        var subscription: Disposable? = null

        val em = this.entityManagerFactory.createEntityManager()

        val tourIds = JPAQuery<TadTour>(em)
                .select(tadTour.id)
                .where(tadTour.stationNo.eq(stationNo))
                .fetch()

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

        val tourRecord = tourRepo.findOne(tadTour.nodeId.eq(nodeRecord.nodeId.toLong()))
                .toNullable()
                ?: throw NoSuchElementException("No tour for this node")

        val tour = this.getById(tourRecord.id.toLong())

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

    override fun create(deliverylistIds: List<Long>): List<Tour> {
        val dlRecords = deliverylistRepository
                .findByIds(deliverylistIds)

        dlRecords.map { it.id.toLong() }.let { deliverylistIds.subtract(it) }.also { missing ->
            if (missing.count() > 0)
                throw RestProblem(
                        status = Status.NOT_FOUND,
                        detail = "One or more delivery lists could not be found [${missing.joinToString(", ")}]")
        }

        val dlDetailRecordsById = deliverylistRepository
                .findDetailsByIds(deliverylistIds)
                .groupBy { it.id }

        val timestamp = Date().toTimestamp()

        val em = this.entityManagerFactory.createEntityManager()

        // Create tour/entries from delivery list
        val tours = em.transaction {
            dlRecords.map { dlRecord ->
                // Create new tour
                val tourRecord = TadTour().also {
                    it.nodeId = null
                    it.userId = null
                    it.stationNo = dlRecord.deliveryStation.toLong()
                    it.deliverylistId = dlRecord.id.toLong()
                    it.uid = UUID.randomUUID().toString()
                    it.timestamp = timestamp

                    em.persist(it)
                    em.flush()
                }

                val dlDetailRecords = dlDetailRecordsById.getValue(dlRecord.id)

                // Transform delivery list detail to tour entry record
                val tourEntryRecords = dlDetailRecords.mapIndexed { index, dlDetailRecord ->
                    TadTourEntry().also {
                        it.tourId = tourRecord.id
                        it.orderId = dlDetailRecord.orderId.toLong()
                        it.orderTaskType = TaskType.valueOf(dlDetailRecord.stoptype).value
                        it.position = (index + 1).toDouble()
                        it.uid = UUID.randomUUID().toString()
                        it.timestamp = timestamp

                        em.persist(it)
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
        private val byId = ConcurrentHashMap<Long, TourOptimizationStatus>()

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
        fun onStart(id: Long) {
            TourOptimizationStatus(id = id, inProgress = true).also { status ->
                this.byId.set(id, status)
                this.updatedSubject.onNext(status)
            }
        }

        /**
         * Should be called when tour optimization finishes
         * @param tourRecord Tour record
         */
        fun onFinish(id: Long) {
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
    private fun TadTour.toTour(
            nodeUid: String? = null,
            orderRecordsById: Map<Long, org.deku.leoz.service.internal.OrderService.Order>? = null,
            tourEntryRecordsByTourId: Map<Long, List<TadTourEntry>>? = null): Tour {
        val tourRecord = this

        @Suppress("NAME_SHADOWING")
        val nodeUid = this.nodeId?.let {
            nodeUid ?: dsl.select(MST_NODE.KEY)
                    .from(MST_NODE)
                    .where(MST_NODE.NODE_ID.eq(tourRecord.nodeId.toInt()))
                    .fetchOne().value1()
        }

        // Fetch tour entries. Equal positions represent stop tasks in order of PK
        val tourEntryRecords = if (tourEntryRecordsByTourId != null)
            tourEntryRecordsByTourId.get(
                    tourRecord.id
            ) ?: listOf()
        else {
            tourEntryRepo.findAll(
                    tadTourEntry.tourId.eq(tourRecord.id)
            )
        }

        val ordersById = if (orderRecordsById != null)
            orderRecordsById
        else
            this@TourServiceV1.orderService.getByIds(
                    tourEntryRecords.map { it.orderId }.distinct()
            )
                    .associateBy { it.id }

        val orders = tourEntryRecords.map { it.orderId }.distinct().map { ordersById.getValue(it) }

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

        val em = this.entityManagerFactory.createEntityManager()

        // Upsert stop list
        em.transaction {
            // Check for existing tour
            val tourRecord = this.tourRepo.findOne(tadTour.nodeId.eq(nodeId.toLong()))
                    .toNullable()
                    ?:
                    // Create new one if it doesn't exist
                    TadTour().also {
                        it.userId = tour.userId
                        it.nodeId = nodeId.toLong()
                        it.uid = UUID.randomUUID().toString()
                        it.timestamp = message.timestamp.toTimestamp()

                        em.persist(it)
                        em.flush()
                    }

            // Recreate tour entries from update
            JPADeleteClause(em, tadTourEntry)
                    .where(tadTourEntry.tourId.eq(tourRecord.id))
                    .execute()

            tour.stops
                    .forEachIndexed { index, stop ->
                        stop.tasks.forEach { task ->
                            TadTourEntry().also {
                                it.tourId = tourRecord.id
                                it.position = (index + 1).toDouble()
                                it.orderId = task.orderId
                                it.orderTaskType = when (task.taskType) {
                                    Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                    Task.Type.PICKUP -> TaskType.DELIVERY.value
                                }
                                it.uid = UUID.randomUUID().toString()
                                it.timestamp = message.timestamp.toTimestamp()

                                em.persist(it)
                            }
                        }
                    }
        }
    }
//endregion
}
