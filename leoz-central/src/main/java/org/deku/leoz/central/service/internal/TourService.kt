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
import org.deku.leoz.model.TaskType
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.id
import org.deku.leoz.smartlane.SmartlaneBridge
import org.deku.leoz.smartlane.model.Routedeliveryinput
import org.deku.leoz.smartlane.model.Routinginput
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
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

    /**
     * Tracks optimizations and provides notifications
     */
    private inner class Optimizations : Iterable<TourServiceV1.TourOptimizationStatus> {
        /** Holds all current optimizations (by tour id) */
        private val byId = ConcurrentHashMap<Int, TourServiceV1.TourOptimizationStatus>()

        private val updatedSubject = PublishSubject.create<TourServiceV1.TourOptimizationStatus>()
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
            TourServiceV1.TourOptimizationStatus(id = id, inProgress = true).also { status ->
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
            TourServiceV1.TourOptimizationStatus(id = id, inProgress = false).also { status ->
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
     * Current optimizations
     */
    private val optimizations = Optimizations()

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
    private lateinit var stationRepository: JooqStationRepository

    @Inject
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var smartlaneBridge: SmartlaneBridge
    //endregion

    /**
     * Create new tours
     * @param tours Tours to create
     * @return Ids of created tours
     */
    fun create(tours: Iterable<TourServiceV1.Tour>): List<Int> {
        return dsl.transactionResult { _ ->
            tours.map { tour ->
                // Create new one if it doesn't exist
                val tourRecord = dsl.newRecord(TAD_TOUR).also {
                    it.userId = tour.userId
                    it.stationId = tour.stationId
                    it.store()
                }

                tour.stops.forEachIndexed { index, stop ->
                    stop.tasks.forEach { task ->
                        dsl.newRecord(TAD_TOUR_ENTRY).also {
                            it.tourId = tourRecord.id
                            it.position = (index + 1).toDouble()
                            it.orderId = task.orderId
                            it.orderTaskType = when (task.taskType) {
                                TourServiceV1.Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                TourServiceV1.Task.Type.PICKUP -> TaskType.DELIVERY.value
                            }
                            it.store()
                        }
                    }
                }

                tourRecord.id
            }
        }
    }

    //region REST
    /**
     * Get tours
     */
    override fun get(
            debitorId: Int?,
            stationId: Int?,
            userId: Int?
    ): List<TourServiceV1.Tour> {
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
                        stationId != null -> it.and(TAD_TOUR.STATION_ID.eq(stationId))
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
                        throw RestProblem(status = Status.NOT_FOUND)
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
    override fun getById(id: Int): TourServiceV1.Tour {
        val tourRecord = tourRepository.findById(id) ?: throw RestProblem(
                status = Status.NOT_FOUND
        )

        val nodeUid = tourRecord.nodeId?.let {
            dsl.selectFrom(MST_NODE)
                    .fetchUidById(tourRecord.nodeId) ?: throw RestProblem(
                    status = Status.NOT_FOUND,
                    detail = "No node uid for id [${tourRecord.nodeId}]"
            )
        }

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get tour by node
     */
    override fun getByNode(nodeUid: String): TourServiceV1.Tour {
        val nodeId = this.nodeRepository.findByKeyStartingWith(nodeUid)?.nodeId
                ?: throw RestProblem(
                        status = Status.NOT_FOUND,
                        detail = "Unknown node uid ${nodeUid}"
                )

        val tourRecord = tourRepository.findByNodeId(nodeId) ?: throw RestProblem(
                status = Status.NOT_FOUND
        )

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get the (current) tour for a user
     */
    override fun getByUser(userId: Int): TourServiceV1.Tour {
        val tourRecord = tourRepository.findLatestByUserId(userId) ?: throw RestProblem(
                status = Status.NOT_FOUND,
                detail = "No assignable tour for user [${userId}]"
        )

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
            options: TourServiceV1.TourOptimizationOptions,
            response: AsyncResponse) {

        data class Optimization(val tourId: Int, val result: List<TourServiceV1.Tour>)

        try {
            log.info("Starting ruote optimization for tour(s) [${ids.joinToString(", ")}] ")

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
            options: TourServiceV1.TourOptimizationOptions,
            response: AsyncResponse) {

        this.optimize(
                ids = listOf(id),
                waitForCompletion = waitForCompletion,
                options = options,
                response = response)
    }

    override fun status(stationId: Int, sink: SseEventSink, sse: Sse) {
        var subscription: Disposable? = null

        //this.stationRepository.findById(stationId)

        val tourIds = dsl.selectFrom(TAD_TOUR)
                .where(TAD_TOUR.STATION_ID.eq(stationId))
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
            options: TourServiceV1.TourOptimizationOptions
    ) {
        val nodeRecord = dsl.selectFrom(MST_NODE)
                .fetchByUid(nodeUid, strict = false)
                ?: throw RestProblem(status = Status.NOT_FOUND, detail = "Node not found")

        val tourRecord = tourRepository.findByNodeId(nodeRecord.nodeId)
                ?: throw RestProblem(status = Status.NOT_FOUND, detail = "No tour for this node")

        try {
            val tour = this.getById(tourRecord.id)

            this.optimize(
                    tour = tour,
                    options = options
            )
                    .subscribeBy(
                            onSuccess = { tours ->
                                JmsEndpoints.node.topic(identityUid = Identity.Uid(nodeRecord.uid))
                                        .channel()
                                        .send(TourServiceV1.TourOptimizationResult(
                                                requestUid = null,
                                                nodeUid = nodeRecord.uid,
                                                tour = tours.first()
                                        ))
                            },
                            onError = { e ->
                                log.error(e.message, e)
                            }
                    )
        } catch (e: Exception) {
            throw RestProblem(
                    status = Status.INTERNAL_SERVER_ERROR,
                    detail = e.message
            )
        }
    }

    override fun create(deliveryListId: Int): TourServiceV1.Tour {
        val dlRecord = deliverylistRepository.findById(
                deliveryListId.toLong()
        ) ?: throw RestProblem(
                status = Status.NOT_FOUND
        )

        val dlDetailRecords = deliverylistRepository.findDetailsById(
                deliveryListId.toLong()
        )

        val timestamp = Date().toTimestamp()

        // Create tour/entries from delivery list
        val tour = dsl.transactionResult { _ ->
            // Create new tour
            val tourRecord = dsl.newRecord(TAD_TOUR).also {
                it.nodeId = null
                it.userId = null
                it.stationId = dlRecord.deliveryStation.toInt()
                it.deliverylistId = deliveryListId
                it.timestamp = timestamp
                it.store()
            }

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

            tourRecord.toTour(
                    tourEntryRecordsByTourId = mapOf(Pair(tourRecord.id, tourEntryRecords))
            )
        }

        return tour
    }
    //endregion

    //region Optimization
    /**
     * Optimize a tour
     * @param tour Tour to optimize
     * @param options Optimization options
     * @return Single observable of optimized tours
     */
    fun optimize(
            tour: TourServiceV1.Tour,
            options: TourServiceV1.TourOptimizationOptions
    ): Single<List<TourServiceV1.Tour>> {
        // Check if optimization for this tour is already in progress
        if (this.optimizations.any { it.id == tour.id })
            throw IllegalStateException("Optimization for tour [${tour.id}] already in progress")

        val tourId = tour.id!!

        return this.smartlaneBridge.optimizeRoute(
                tour.toRoutingInput(
                        options
                ))
                .doOnSubscribe {
                    this.optimizations.onStart(tourId)
                }
                .map { routes ->
                    routes.map { route ->
                        val stops = route.deliveries
                                .sortedBy { it.orderindex }
                                .map { delivery ->
                                    tour.stops.first { it.id == delivery.customId.toInt() }
                                }

                        val orders = stops
                                .flatMap { it.tasks }
                                .map { it.orderId }.distinct()
                                .map { orderId -> tour.orders.first { it.id == orderId } }

                        TourServiceV1.Tour(
                                id = null,
                                nodeUid = tour.nodeUid,
                                userId = tour.userId,
                                stationId = tour.stationId,
                                stops = stops,
                                orders = orders
                        )
                    }
                }
                .doFinally {
                    this.optimizations.onFinish(tourId)
                }
                .firstOrError()
    }

    /**
     * Update existing tour from optimized one
     * @param tour Optimized tour
     */
    fun updateFromOptimizedTour(tour: TourServiceV1.Tour) {
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
    //endregion

    //region Transformations
    /**
     * Transform tour into smartlane routing input
     */
    private fun TourServiceV1.Tour.toRoutingInput(
            options: TourServiceV1.TourOptimizationOptions
    ): Routinginput {
        return Routinginput().also {
            val shiftOffset by lazy {
                Duration.ofHours(
                        options.appointments.shiftHoursFromNow?.toLong() ?: 0)
            }

            it.deliverydata = this.stops
                    .map { stop ->
                        Routedeliveryinput().also {
                            stop.address?.also { address ->
                                it.contactlastname = address.line1
                                it.contactfirstname = address.line2
                                it.contactcompany = address.line3
                                it.street = address.street
                                it.housenumber = address.streetNo
                                it.city = address.city
                                it.postalcode = address.zipCode
                                it.load = "${stop.weight}kg"
                                address.geoLocation?.also { geo ->
                                    it.lat = geo.latitude.toString()
                                    it.lng = geo.longitude.toString()
                                }
                            }

                            // Track stop via custom id
                            it.customId = stop.id?.toString()

                            it.load = options.vehicles
                                    ?.map { "${it.capacity}kg" }
                                    ?.joinToString(",")

                            if (!options.appointments.omit) {
                                it.pdtFrom = stop.appointmentStart
                                it.pdtTo = stop.appointmentEnd
                            }
                        }
                    }
                    .also {
                        // Current time
                        val now = Date()

                        if (!options.appointments.omit) {

                            if (options.appointments.replaceDatesWithToday) {
                                it.forEach {
                                    it.pdtFrom = it.pdtFrom?.replaceDate(now)
                                    it.pdtTo = it.pdtTo?.replaceDate(now)
                                }
                            }

                            if (options.appointments.shiftHoursFromNow != null) {
                                val earliestAppointmentTime by lazy {
                                    (it.mapNotNull { it.pdtFrom }.min() ?: Date()).toLocalDateTime()
                                }

                                fun Date.shiftAppointmentTime(): Date {
                                    return now.toLocalDateTime()
                                            // Round to next full hour
                                            .truncatedTo(ChronoUnit.HOURS)
                                            .plus(Duration.ofHours(1))
                                            // Add duration between earlist appointment time and this one
                                            .plus(Duration.between(
                                                    earliestAppointmentTime,
                                                    this.toLocalDateTime()))
                                            // Add shift offset
                                            .plus(shiftOffset)
                                            .toDate()
                                }

                                it.forEach {
                                    it.pdtFrom = it.pdtFrom?.shiftAppointmentTime()
                                    it.pdtTo = it.pdtTo?.shiftAppointmentTime()
                                }
                            }

                            // Sanity checks
                            it.forEach {
                                it.pdtTo = if (it.pdtTo != null && it.pdtTo > now) it.pdtTo else null
                                it.pdtFrom = if (it.pdtTo != null) it.pdtFrom else null

                                log.trace { "PDT ${it.pdtFrom} -> ${it.pdtTo}" }
                            }
                        }
                    }
        }
    }

    private fun Iterable<TadTourRecord>.toTours(): List<TourServiceV1.Tour> {
        TODO("not implemented")
    }

    /**
     * Transform tour/node record into service entity
     * @param nodeUid OPTIONAL node record referring to this tour. If not provided it will be looked up.
     * @param tourEntryRecordsByTourId OPTIONAL tour entry record map for fast lookups
     */
    private fun TadTourRecord.toTour(
            nodeUid: String? = null,
            tourEntryRecordsByTourId: Map<Int, List<TadTourEntryRecord>>? = null): TourServiceV1.Tour {
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

        return TourServiceV1.Tour(
                id = tourRecord.id,
                nodeUid = nodeUid,
                userId = tourRecord.userId,
                stationId = tourRecord.stationId,
                orders = orders,
                stops = tourEntryRecords.groupBy { it.position }
                        .map { stop ->
                            val tasks = stop.value.map { task ->
                                val taskType = TaskType.valueMap.getValue(task.orderTaskType)
                                val order = ordersById.getValue(task.orderId)

                                TourServiceV1.Task(
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
                                            TaskType.DELIVERY -> TourServiceV1.Task.Type.DELIVERY
                                            TaskType.PICKUP -> TourServiceV1.Task.Type.PICKUP
                                        }
                                )
                            }

                            TourServiceV1.Stop(
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
            TourServiceV1.TourUpdate::class,
            TourServiceV1.TourOptimizationRequest::class
    )
    override fun onMessage(message: Any, replyChannel: MqChannel?) {
        when (message) {
            is TourServiceV1.TourUpdate -> {
                this.onMessage(message)
            }
            is TourServiceV1.TourOptimizationRequest -> {
                this.onMessage(message)
            }
        }
    }

    /**
     * Tour optimization request message handler
     */
    private fun onMessage(message: TourServiceV1.TourOptimizationRequest) {
        val nodeUid = message.nodeUid ?: run {
            log.warn("Tour optimization request received without node uid")
            return
        }

        this.optimizeForNode(
                nodeUid = nodeUid,
                options = message.options
        )
    }

    /**
     * Tour update message handler
     */
    private fun onMessage(message: TourServiceV1.TourUpdate) {
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
                                    TourServiceV1.Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                    TourServiceV1.Task.Type.PICKUP -> TaskType.DELIVERY.value
                                }
                                it.store()
                            }
                        }
                    }
        }
    }
//endregion
}
