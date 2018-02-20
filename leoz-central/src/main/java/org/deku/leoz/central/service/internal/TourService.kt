package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TaskType
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.QTadTourEntry.tadTourEntry
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.deku.leoz.node.data.repository.StationRepository
import org.deku.leoz.node.data.repository.TadTourEntryRepository
import org.deku.leoz.node.data.repository.TadTourRepository
import org.deku.leoz.node.data.repository.toAddress
import org.deku.leoz.node.service.internal.SmartlaneBridge
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.TourServiceV1.*
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.zalando.problem.Status
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.persistence.querydsl.batchDelete
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.transaction
import sx.persistence.withEntityManager
import sx.rs.RestProblem
import sx.rs.push
import sx.time.minusHours
import sx.time.plusDays
import sx.time.plusMinutes
import sx.time.toTimestamp
import sx.util.hashWithSha1
import sx.util.letWithParamNotNull
import sx.util.toNullable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
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

    //region Dependencies
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

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
    private lateinit var stationRepo: StationRepository

    @Inject
    private lateinit var orderService: OrderService
    @Inject
    private lateinit var locationService: LocationServiceV2

    @Inject
    private lateinit var smartlane: SmartlaneBridge

    /** Object mapper used for (de-)serializing (route) metas from/to persistence store */
    private val objectMapper: ObjectMapper by lazy {
        ObjectMapper().also {
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            it.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }
    //endregion

    private val updatedSubject = PublishSubject.create<SubscriptionEvent>()
    /** Tour update / subscription event */
    val updated = this.updatedSubject.hide()

    fun createUid(): String = UUID.randomUUID().hashWithSha1(10)

    @PostConstruct
    fun onInitialize() {
        // Location received event
        this.locationService.locationReceived
                .subscribe { gpsMessage ->
                    try {
                        val userId = gpsMessage.userId ?: run {
                            // Skip location updates without user id
                            return@subscribe
                        }

                        val user = this.userRepository.findById(userId)
                                ?: throw NoSuchElementException("User id [${userId}]")

                        if (smartlane.hasDriver(user.email)) {
                            val positions = gpsMessage.dataPoints?.toList() ?: listOf()

                            this.smartlane.putDriverPosition(
                                    email = user.email,
                                    positions = positions
                            )
                                    .subscribeBy(
                                            onError = { e -> log.error(e.message, e) }
                                    )
                        }
                    } catch (e: Throwable) {
                        log.error("Location push to routing provider failed. node [${gpsMessage.nodeKey}], user [${gpsMessage.userId}]: ${e.message}")
                    }
                }
    }

    @Scheduled(cron = "0 0 */2 * * *")
    fun clean() {
        val expiry = Date()
                .plusDays(-1)
                .toTimestamp()

        emf.withEntityManager { em ->
            this.delete(ids = em.from(tadTour).select(tadTour.id)
                    .where(tadTour.created.lt(expiry))
                    .fetch()
                    .also {
                        log.info("Removing expired tours [${it.joinToString(", ")}]")
                    }
            )
        }
    }

    /**
     * Update or create tour(s) from domain instance(s)
     * @param tours Tours to update or create
     */
    fun put(
            tours: Iterable<Tour>
    ): List<Tour> {
        val now = Date().toTimestamp()

        if (tours.count() == 0)
            return

        return emf.transaction { em ->
            tours
                    .map { tour ->
                        //region Update tour record
                        val nodeUid = tour.nodeUid

                        // Lookup tour by node uid if applicable
                        if (nodeUid != null) {
                            val nodeId = dsl.selectFrom(MST_NODE)
                                    .fetchIdByUid(nodeUid)
                                    ?: throw NoSuchElementException("Node with uid ${nodeUid} doesn't exist")

                            tour.id = this.tourRepo.findOne(tadTour.nodeId.eq(nodeId.toLong()))
                                    .toNullable()
                                    ?.id
                        }

                        val tourExists = tour.id != null

                        // Lookup tour by id or create new one
                        val tourRecord = if (tourExists)
                            this.tourRepo.findOne(tadTour.id.eq(tour.id))
                                    .toNullable()
                                    ?: throw NoSuchElementException("Tour with id ${tour.id} doesn't exist")
                        else
                            TadTour().also {
                                it.id = tour.id
                                it.uid = tour.uid ?: this.createUid()
                                it.created = now
                            }

                        // Update tour record field
                        tourRecord.also { r ->
                            r.userId = tour.userId
                            r.nodeId = tour.nodeUid?.let { nodeUid ->
                                // TODO: this look is expensive. replicate mst_node to h2 or reference nodes by uid instead of id
                                dsl.selectFrom(MST_NODE).fetchIdByUid(nodeUid)?.toLong()
                            }
                            r.stationNo = tour.stationNo
                            r.parentId = tour.parentId
                            r.customId = tour.customId
                            r.optimized = tour.optimized?.toTimestamp()
                            r.date = tour.date?.toString()

                            r.modified = now

                            tour.route?.also { route ->
                                r.routeMeta = this.objectMapper.writeValueAsString(route)
                            }
                        }

                        // Persist or merge tour record
                        if (tourExists) {
                            em.merge(tourRecord)
                        } else {
                            em.persist(tourRecord)
                            // Flush so generated id is reflected
                            em.flush()

                            tour.id = tourRecord.id
                            tour.uid = tourRecord.uid
                        }
                        //endregion

                        //region Update tour entry records
                        val tourEntryIds = tour.stops.flatMap { it.tasks }.map { it.id }

                        val entryRecords = if (tourExists)
                            tourEntryRepo.findAll(
                                    tadTourEntry.tourId.eq(tour.id),
                                    tadTourEntry.position.asc(),
                                    tadTourEntry.id.asc())
                                    .toMutableList()
                        else
                            mutableListOf()

                        if (entryRecords.count() > 0) {
                            // Delete existing tour entries which are not referenced in updated tour
                            val toDelete = entryRecords
                                    .filter { !tourEntryIds.contains(it.id) }

                            if (toDelete.count() > 0) {
                                entryRecords.removeAll(toDelete)
                                tourEntryRepo.deleteAll(toDelete)
                            }
                        }

                        tour.stops.forEachIndexed { stopIndex, stop ->
                            val newPosition = (stopIndex + 1).toDouble()

                            val route = stop.route?.let {
                                this.objectMapper.writeValueAsString(it)
                            }

                            // Create and update tour entries
                            stop.tasks.forEachIndexed { taskIndex, task ->
                                val taskExists = tourExists && task.id != null

                                val entryRecord = if (taskExists)
                                    entryRecords.first { it.id == task.id }
                                else
                                    TadTourEntry().also { r ->
                                        r.tourId = tour.id
                                        r.uid = task.uid ?: this.createUid()
                                    }

                                entryRecord.also { r ->
                                    r.position = newPosition
                                    r.orderId = task.orderId
                                    r.orderTaskType = when (task.taskType) {
                                        Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                        Task.Type.PICKUP -> TaskType.DELIVERY.value
                                    }
                                    r.timestamp = now

                                    if (taskIndex == 0)
                                        r.routeMeta = route
                                }

                                if (taskExists) {
                                    em.merge(entryRecord)
                                } else {
                                    em.persist(entryRecord)
                                    em.flush()

                                    task.id = entryRecord.id
                                    task.uid = entryRecord.uid
                                }
                            }
                        }
                        //endregion

                        tourRecord.id.toLong()
                    }
        }
                .let { tourIds ->
                    this.get(tourIds).also {
                        it
                                .groupBy { it.stationNo }
                                .forEach { stationNo, tours ->
                                    this.updatedSubject.onNext(SubscriptionEvent(
                                            stationNo = stationNo,
                                            items = tours.toList()
                                    ))
                                }
                    }
                }
    }

    //region REST
    /**
     * Get tours
     * @throws NoSuchElementException When no tours have been found
     */
    override fun get(
            ids: List<Long>?,
            debitorId: Long?,
            stationNo: Long?,
            userId: Long?,
            from: ShortDate?,
            to: ShortDate?
    ): List<Tour> {
        val tourRecords =
                this.tourRepo.findAll(BooleanBuilder()
                        .letWithParamNotNull(ids, { and(tadTour.id.`in`(ids)) })
                        .letWithParamNotNull(debitorId, {
                            and(tadTour.userId.`in`(
                                    userRepository.findUserIdsByDebitor(it.toInt()).map { it.toLong() }
                            ))
                        })
                        .letWithParamNotNull(stationNo, { and(tadTour.stationNo.eq(it)) })
                        .letWithParamNotNull(userId, { and(tadTour.userId.eq(it)) })
                        .letWithParamNotNull(from, { and(tadTour.date.goe(it.toString())) })
                        .letWithParamNotNull(to, { and(tadTour.date.loe(it.toString())) })
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
        return this.emf.withEntityManager { em ->
            // Get latest tour for user
            val tourRecord = em.from(tadTour)
                    .where(tadTour.userId.eq(userId))
                    .orderBy(tadTour.modified.desc())
                    .fetchFirst()
                    ?: throw NoSuchElementException("No assignable tour for user [${userId}]")

            tourRecord.toTour()
        }
    }

    override fun delete(
            ids: List<Long>?,
            userId: Long?,
            stationNo: Long?,
            includeRelated: Boolean?) {

        emf.transaction { em ->
            em.from(tadTour)
                    .select(tadTour.id, tadTour.stationNo, tadTour.uid)
                    .let {
                        when {
                            ids != null -> {
                                it.where(tadTour.id.`in`(ids))
                            }
                            else -> it
                        }
                    }
                    .fetch()
                    .plus(stationNo?.let {
                        em.from(tadTour)
                                .select(tadTour.id, tadTour.stationNo, tadTour.uid)
                                .where(tadTour.stationNo.eq(it))
                                .fetch()

                    } ?: listOf())
                    .plus(userId?.let {
                        em.from(tadTour)
                                .select(tadTour.id, tadTour.stationNo, tadTour.uid)
                                .where(tadTour.userId.eq(it))
                                .fetch()
                    } ?: listOf<Tuple>())
                    .let {
                        when (includeRelated ?: false) {
                            true -> {
                                val tourIds = it.map { it.get(tadTour.id) }

                                it.plus(
                                        em.from(tadTour)
                                                .select(tadTour.id, tadTour.stationNo, tadTour.uid)
                                                .where(tadTour.parentId.`in`(tourIds))
                                                .fetch()
                                )
                            }
                            false -> it
                        }
                    }
                    .also { records ->
                        if (records.count() > 0) {
                            val tourIds = records
                                    .map { it.get(tadTour.id) }
                                    .distinct()

                            em.delete(
                                    tadTourEntry,
                                    tadTourEntry.tourId.`in`(tourIds))

                            em.delete(
                                    tadTour,
                                    tadTour.id.`in`(tourIds)
                            )

                            this.smartlane.deleteRoutes(
                                    tours = records.map {
                                        Tour(
                                                id = it.get(tadTour.id),
                                                uid = it.get(tadTour.uid)
                                        )
                                    }
                            )
                                    .subscribeBy(onError = { e -> log.error(e.message, e) })
                        }
                    }
        }
                .also { records ->
                    records
                            .groupBy { it.get(tadTour.stationNo) }
                            .forEach { stationNo, groupedRecords ->
                                this.updatedSubject.onNext(SubscriptionEvent(
                                        stationNo = stationNo,
                                        deleted = groupedRecords.map { it.get(tadTour.id)!! }
                                ))
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
                                    // Reset ids for all optimized tours, enforce create
                                    tours.forEach { it.id = null }

                                    // Create tours from optimized results
                                    this.put(tours).also {
                                        // Update custom ids @smartlane
                                        this.smartlane.updateRoutes(it)
                                                .subscribeBy(onError = { e -> log.error(e.message, e) })
                                    }

                                } else {
                                    // Update tour in place
                                    this.put(
                                            tours = listOf(tours.first())
                                    )
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
        this.emf.withEntityManager { em ->
            val tourIds = em.from(tadTour)
                    .select(tadTour.id)
                    .where(tadTour.stationNo.eq(stationNo))
                    .fetch()

            sink.push(
                    sse = sse,
                    events = this.optimizations.updated
                            .filter { tourIds.contains(it.id) },
                    onError = { log.error(it.message) }
            )
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
     *
     * The optimized tour will NOT be stored persistently, but sent back straight to the requesting node via jms.
     *
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
            if (options.vehicles?.count() ?: 0 > 1)
                throw IllegalArgumentException("Multiple vehicles are not supported when " +
                        "optimizing a single (node related) tour")

            val tourRecord = tourRepo.findOne(tadTour.nodeId.eq(nodeRecord.nodeId.toLong()))
                    .toNullable()
                    ?: throw NoSuchElementException("No tour for node [${nodeUid}]")

            val tour = this.getById(tourRecord.id.toLong())

            val userId = tour.userId?.toInt()
                    ?: throw IllegalArgumentException("Node tour is missing user id")

            val userRecord = this.userRepository.findById(userId)
                    ?: throw IllegalArgumentException("Invalid user id [${userId}]")

            // Update smartlane driver
            this.smartlane.putDriver(
                    user = userRecord.toUser()
            )
                    .blockingAwait()

            this.optimize(
                    tour = tour,
                    options = options
            )
                    .subscribeBy(
                            onSuccess = { tours ->
                                val optimizedTour = tours.first()

                                JmsEndpoints.node.topic(identityUid = Identity.Uid(nodeRecord.uid))
                                        .channel()
                                        .send(TourOptimizationResult(
                                                requestUid = nodeRequestUid,
                                                nodeUid = nodeRecord.uid,
                                                tour = optimizedTour
                                        ))

                                this.smartlane.assignDriver(
                                        email = userRecord.email,
                                        tour = optimizedTour
                                )
                                        .subscribeBy({ e -> log.error(e.message, e) })
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

        val dlDetailRecordsByDlId = deliverylistRepository
                .findDetailsByIds(deliverylistIds)
                .groupBy { it.id }

        val tours =
                this.put(tours = dlRecords.map { dlRecord ->
                    Tour(
                            stationNo = dlRecord.deliveryStation.toLong(),
                            customId = dlRecord.id.toLong().toString(),
                            date = ShortDate(dlRecord.deliveryListDate),
                            stops = dlDetailRecordsByDlId.getValue(dlRecord.id)
                                    .sortedBy { it.orderPosition }
                                    .groupBy { it.orderPosition }
                                    .map { dlStop ->
                                        Stop(
                                                tasks = dlStop.value
                                                        .distinctBy { it.orderId.toString() + it.stoptype }
                                                        .map { dlDetailRecord ->
                                                            Task(
                                                                    orderId = dlDetailRecord.orderId.toLong(),
                                                                    taskType = when (TaskType.valueOf(dlDetailRecord.stoptype)) {
                                                                        TaskType.PICKUP -> Task.Type.PICKUP
                                                                        TaskType.DELIVERY -> Task.Type.DELIVERY
                                                                    }
                                                            )
                                                        }

                                        )

                                    }
                    )
                })

        return tours
    }

    override fun subscribe(stationNo: Long, sink: SseEventSink, sse: Sse) {
        this.emf.withEntityManager { em ->
            sink.push(
                    sse = sse,
                    events = this.updated
                            .filter { it.stationNo == stationNo },
                    onError = { log.error(it.message) }
            )
        }
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

        if (options.start == null) {
            // Complement start address if applicable
            tour.stationNo?.also { stationNo ->
                val station = this.stationRepo.findByStation(stationNo.toInt())
                        ?: throw NoSuchElementException("Station no [${stationNo}] doesn't exist")

                options.start = station.toAddress()
            }
        }

        return this.smartlane.optimize(
                tour = tour,
                options = options,
                uidSupplier = { this.createUid() }
        )
                .doOnSubscribe {
                    this.optimizations.onStart(tourId)
                }
                .doFinally {
                    this.optimizations.onFinish(tourId)
                }
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
            log.warn("Missing node uid")
            return
        }

        message.startStationNo?.also { stationNo ->
            val station = stationRepo.findByStation(stationNo)
            when (station) {
                null -> log.warn("Station no [${stationNo}] doesn't exist")
                else -> {
                    // Override start address with station
                    message.options.start = station.toAddress()
                }
            }
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

        try {
            // Set tour date to message timestamp (nodes may not provide this)
            tour.date = ShortDate(message.timestamp)

            // Update tour
            this.put(listOf(tour))

        } catch (e: Throwable) {
            log.error(e.message)
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
            )
                    ?.sortedBy { it.position }
                    ?.sortedWith(compareBy<TadTourEntry> { it.position }.thenBy { it.id })
                    ?: listOf()
        else {
            tourEntryRepo.findAll(
                    tadTourEntry.tourId.eq(tourRecord.id),
                    tadTourEntry.position.asc(),
                    tadTourEntry.id.asc()
            )
        }

        // Order lookup table by id
        val ordersById = if (orderRecordsById != null)
            orderRecordsById
        else
            this@TourServiceV1.orderService.getByIds(
                    tourEntryRecords.map { it.orderId }.distinct()
            )
                    .associateBy { it.id }

        // Gather orders for this tour
        val orders = tourEntryRecords.map { it.orderId }.distinct()
                .map { ordersById.getValue(it) }

        return Tour(
                id = tourRecord.id,
                uid = tourRecord.uid,
                nodeUid = nodeUid,
                userId = tourRecord.userId,
                stationNo = tourRecord.stationNo,
                parentId = tourRecord.parentId,
                customId = tourRecord.customId,
                created = tourRecord.created,
                date = ShortDate(tourRecord.date),
                optimized = tourRecord.optimized,
                orders = orders,
                route = tourRecord.routeMeta?.let {
                    objectMapper.readValue(it, TourRouteMeta::class.java)
                },
                stops = tourEntryRecords.groupBy { it.position }
                        .map { stop ->
                            val tasks = stop.value.map { task ->
                                val taskType = TaskType.valueMap.getValue(task.orderTaskType)
                                val order = ordersById.getValue(task.orderId)

                                Task(
                                        id = task.id,
                                        uid = task.uid,
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
                                            .sumByDouble { it.dimension.weight },
                                    route = stop.value.first().routeMeta?.let {
                                        this@TourServiceV1.objectMapper.readValue(it, TourStopRouteMeta::class.java)
                                    }
                            )
                        }
        )
    }
    //endregion
}