package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Completable
import io.reactivex.Observable
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
import org.zalando.problem.Status
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.rs.DefaultProblem
import sx.time.replaceDate
import sx.time.toTimestamp
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ContextResolver
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

        override fun iterator(): Iterator<TourServiceV1.TourOptimizationStatus>
                = this.byId.values.iterator()

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

    @Inject
    private lateinit var deliverylistRepository: JooqDeliveryListRepository

    @Inject
    private lateinit var tourRepository: JooqTourRepository

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var smartlaneBridge: SmartlaneBridge

    @Context
    private lateinit var objectMapperResolver: ContextResolver<ObjectMapper>
    //endregion

    //region REST
    /**
     * Get tours
     */
    override fun get(
            debitorId: Int?,
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
                        userId != null -> it.and(TAD_TOUR.USER_ID.eq(userId))
                        else -> it
                    }
                }
                .fetchArray()
                .also {
                    if (it.size == 0) {
                        // No tour records in selection
                        throw DefaultProblem(status = Status.NOT_FOUND)
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
        val tourRecord = tourRepository.findById(id) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        val nodeUid = tourRecord.nodeId?.let {
            dsl.selectFrom(MST_NODE)
                    .fetchUidById(tourRecord.nodeId) ?:
                    throw DefaultProblem(
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
        val nodeId = dsl.selectFrom(MST_NODE)
                .where(MST_NODE.KEY.eq(nodeUid))
                .fetchOne(MST_NODE.NODE_ID) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "Unknown node uid ${nodeUid}"
                )

        val tourRecord = tourRepository.findByNodeId(nodeId) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        return tourRecord.toTour(nodeUid)
    }

    /**
     * Get the (current) tour for a user
     */
    override fun getByUser(userId: Int): TourServiceV1.Tour {
        val tourRecord = tourRepository.findLatestByUserId(userId) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "No assignable tour for user [${userId}]"
                )

        return tourRecord.toTour()
    }

    /**
     * Optimize a tour and update tour entry positions in place
     * @param id Tour id
     */
    fun optimize(
            id: Int,
            optimizationOptions: TourServiceV1.TourOptimizationOptions
    ): Completable {
        // Check if optimization for this tour is already in progress
        if (this.optimizations.any { it.id == id })
            throw IllegalStateException("Optimization for tour [${id}] already in progress")

        val tour = this.getById(id)
        val tourId = tour.id!!

        return this.smartlaneBridge.optimizeRoute(
                tour.toRoutingInput(
                        optimizationOptions
                ))
                .doOnSubscribe {
                    this.optimizations.onStart(tourId)
                }
                .doOnNext { route ->
                    dsl.transaction { _ ->
                        val now = Date().toTimestamp()
                        val entryRecords = tourRepository.findEntriesById(id)

                        route.deliveries
                                .sortedBy { it.orderindex }
                                .forEachIndexed { index, delivery ->
                                    // Select stop tour entry which matches custom id
                                    val stop = tour.stops.first { it.id == delivery.customId.toInt() }

                                    val entryRecord = entryRecords.first { it.id == stop.id }

                                    val newPosition = (index + 1).toDouble()
                                    log.trace { "OPTIMIZATION POSITION UPDATE ${entryRecord.position} -> ${newPosition} (${delivery.orderindex})" }

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
                                .where(TAD_TOUR.ID.eq(id))
                                .execute()
                    }
                }
                .doOnComplete {
                    this.optimizations.onFinish(tourId)
                }
                .doOnError {
                    this.optimizations.onFinish(tourId)
                }
                .ignoreElements()
    }

    /**
     * Optimize tour
     * @param id Tour id
     * @param response Asynchronous response
     */
    override fun optimize(
            id: Int,
            optimizationOptions: TourServiceV1.TourOptimizationOptions, response: AsyncResponse) {

        try {
            this.optimize(
                    id,
                    optimizationOptions
            )
                    .subscribeBy(
                            onComplete = {
                                response.resume(Response
                                        .status(Response.Status.OK)
                                        .build()
                                )
                            },
                            onError = { e ->
                                log.error(e.message, e)
                                response.resume(DefaultProblem(
                                        status = Status.INTERNAL_SERVER_ERROR,
                                        detail = e.message
                                ))
                            }
                    )
        } catch (e: Exception) {
            throw DefaultProblem(
                    status = Status.INTERNAL_SERVER_ERROR,
                    detail = e.message
            )
        }
    }

    override fun status(ids: List<Int>, sink: SseEventSink, sse: Sse) {
        var subscription: Disposable? = null

        val objectMapper = this.objectMapperResolver.getContext(null)

        subscription = this.optimizations.updated
                // TODO: eventually may have to emit dummy in interval to poll for passive client close
                .takeUntil { sink.isClosed }
                // Filter only relevant tours for optimization updates
                .filter { ids.contains(it.id) }
                .doOnComplete {
                    log.trace("SSE COMPLETED")
                }
                .doOnDispose {
                    log.trace("SSE DISPOSED")
                }
                .subscribe {
                    // Send SSE event for each update
                    sink.send(sse.newEvent(objectMapper.writeValueAsString(it)))
                            .exceptionally {
                                // Close down and unsubscribe on send errors
                                subscription?.dispose()
                                sink.close()
                                null
                            }
                }
    }

    override fun optimizeForNode(
            nodeUid: String,
            optimizationOptions: TourServiceV1.TourOptimizationOptions
    ) {
        val node = dsl.selectFrom(MST_NODE)
                .fetchByUid(nodeUid, strict = false)
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "Node not found"
                )

        val tour = tourRepository.findByNodeId(node.nodeId)
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "Tour not found"
                )

        try {
            this.optimize(
                    id = tour.id,
                    optimizationOptions = optimizationOptions
            )
                    .subscribeBy(
                            onComplete = {
                                JmsEndpoints.node.topic(identityUid = Identity.Uid(node.uid))
                                        .channel()
                                        .send(TourServiceV1.TourUpdate(
                                                tour = this.getById(tour.id)
                                        ))
                            },
                            onError = { e ->
                                log.error(e.message, e)
                                JmsEndpoints.node.topic(identityUid = Identity.Uid(node.uid))
                                        .channel()
                                        .send(TourServiceV1.TourOptimizationError())
                            }
                    )
        } catch (e: Exception) {
            throw DefaultProblem(
                    status = Status.INTERNAL_SERVER_ERROR,
                    detail = e.message
            )
        }
    }

    /**
     * Create tour
     */
    override fun create(
            request: TourServiceV1.TourFromDeliverylist
    ): TourServiceV1.Tour {

        val deliveryListId = request.deliveryListId?.toLong()
                ?: throw DefaultProblem(
                status = Status.BAD_REQUEST,
                detail = "Delivery list id is mandatory"
        )

        val userId = request.userId
                ?: throw DefaultProblem(
                status = Status.BAD_REQUEST,
                detail = "User id is mandatory"
        )

        val dlDetailRecords = deliverylistRepository.findDetailsById(
                deliveryListId
        )

        val timestamp = Date().toTimestamp()

        // Create tour/entries from delivery list
        val tour = dsl.transactionResult { _ ->
            // Create new tour
            val tourRecord = dsl.newRecord(TAD_TOUR).also {
                it.nodeId = null
                it.userId = userId
                it.deliverylistId = request.deliveryListId
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

    //region Transformations
    /**
     * Transform tour into smartlane routing input
     */
    private fun TourServiceV1.Tour.toRoutingInput(
            optimizationOptions: TourServiceV1.TourOptimizationOptions
    ): Routinginput {
        return Routinginput().also {
            it.deliverydata = this.stops.map { stop ->
                Routedeliveryinput().also {
                    stop.address?.also { address ->
                        it.contactlastname = address.line1
                        it.contactfirstname = address.line2
                        it.contactcompany = address.line3
                        it.street = address.street
                        it.housenumber = address.streetNo
                        it.city = address.city
                        it.postalcode = address.zipCode
                        address.geoLocation?.also { geo ->
                            it.lat = geo.latitude.toString()
                            it.lng = geo.longitude.toString()
                        }
                    }

                    // Current time
                    val now = Date()

                    //region Determine pdt parameters
                    var pdtFrom = stop.appointmentStart
                    var pdtTo = stop.appointmentEnd
                    if (optimizationOptions.amendAppointmentTimes) {
                        pdtFrom = pdtFrom?.replaceDate(now)
                        pdtTo = pdtTo?.replaceDate(now)
                    }
                    pdtTo = if (pdtTo != null && pdtTo > now) pdtTo else null
                    pdtFrom = if (pdtTo != null) pdtFrom else null
                    //endregion

                    it.pdtFrom = pdtFrom
                    it.pdtTo = pdtTo

                    log.trace("PDT ${it.pdtFrom} -> ${it.pdtTo}")

                    // Track stop via custom id
                    it.customId = stop.id?.toString()
                }
            }
        }
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
                                    appointmentEnd = tasks.map { it.appointmentEnd }.filterNotNull().min()
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
                val nodeUid = message.nodeUid ?: run {
                    log.warn("Tour optimization request received without node uid")
                    return
                }

                this.optimizeForNode(
                        nodeUid = nodeUid,
                        optimizationOptions = message.optimizationOptions
                )
            }
        }
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
        }
    }
    //endregion
}
