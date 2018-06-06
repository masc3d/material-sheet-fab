package org.deku.leoz.node.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.querydsl.core.BooleanBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TaskType
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.model.VehicleType
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.QTadTourEntry.tadTourEntry
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.deku.leoz.node.data.repository.*
import org.deku.leoz.node.service.smartlane.SmartlaneBridge
import org.deku.leoz.time.ShortDate
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.TourServiceV1.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.transaction
import sx.persistence.withEntityManager
import sx.rs.RestProblem
import sx.rs.push
import sx.time.plusDays
import sx.time.replaceDate
import sx.time.toTimestamp
import sx.util.letWithItems
import sx.util.letWithNotNull
import sx.util.toNullable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.inject.Inject
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
@Component
@Path("internal/v1/tour")
class TourServiceV1
    :
        org.deku.leoz.service.internal.TourServiceV1,
        MqHandler<Any> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    @Inject
    private lateinit var tourRepo: TadTourRepository
    @Inject
    private lateinit var tourEntryRepo: TadTourEntryRepository
    @Inject
    private lateinit var stationRepo: StationRepository
    @Inject
    private lateinit var stationContractRepo: StationContractRepository

    @Inject
    private lateinit var userService: org.deku.leoz.service.internal.UserService
    @Inject
    private lateinit var nodeService: org.deku.leoz.service.internal.NodeServiceV1
    @Inject
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var smartlane: SmartlaneBridge

    /** Object mapper used for (de-)serializing (route) metas from/to persistence store */
    private val objectMapper: ObjectMapper by lazy {
        ObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
    //endregion

    private val updatedSubject = PublishSubject.create<SubscriptionEvent>()
    /** Tour update / subscription event */
    val updated = this.updatedSubject.hide()

    @PostConstruct
    fun onInitialize() {

    }

    @Scheduled(cron = "0 0 */2 * * *")
    fun clean() {
        try {
            val expiry = Date()
                    .plusDays(-4)
                    .toTimestamp()

            emf.withEntityManager { em ->
                em.from(tadTour).select(tadTour.id)
                        .where(tadTour.created.lt(expiry))
                        .fetch()
                        .also {
                            if (it.count() > 0) {
                                log.info("Removing expired tours [${it.joinToString(", ")}]")
                                this.delete(ids = it)
                            }
                        }
            }
        } catch (e: Throwable) {
            log.error("Tour cleanup failed: ${e.message}", e)
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
            return listOf()

        return emf.transaction { em ->
            tours
                    .map { tour ->
                        //region Update tour record
                        val date = tour.date?.date ?: throw IllegalArgumentException("Tour is missing date")

                        val nodeUid = tour.nodeUid
                        val nodeUUid = nodeUid?.let { UUID.fromString(it) }

                        // Lookup tour by node uid if applicable
                        if (nodeUid != null) {
                            this.tourRepo.findOne(tadTour.nodeUid.eq(nodeUUid))
                                    .toNullable()
                                    ?.also {
                                        tour.id = it.id
                                        tour.uid = it.uid.toString()
                                    }
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
                                it.uid = tour.uid?.let { UUID.fromString(it) } ?: UUID.randomUUID()
                                it.created = now
                            }

                        // Update tour record field
                        tourRecord.also { r ->
                            r.userId = tour.userId
                            r.nodeUid = nodeUUid
                            r.stationNo = tour.stationNo
                            r.parentId = tour.parentId
                            r.customId = tour.customId
                            r.optimized = tour.optimized?.toTimestamp()
                            r.date = ShortDate(date).toString()
                            r.vehicleType = tour.vehicleType?.value

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
                            tour.uid = tourRecord.uid.toString()
                        }
                        //endregion

                        //region Update tour entry records
                        val stops = tour.stops

                        if (stops != null) {
                            val tourEntryIds = stops.flatMap { it.tasks }.map { it.id }

                            val entryRecords = if (tourExists)
                                tourEntryRepo.findAll(
                                        tadTourEntry.tourUid.eq(UUID.fromString(tour.uid)),
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

                            stops.forEachIndexed { stopIndex, stop ->
                                val newPosition = (stopIndex + 1).toDouble()

                                val route = stop.route?.let {
                                    this.objectMapper.writeValueAsString(it)
                                }

                                // Create and update tour entries
                                stop.tasks.forEachIndexed { taskIndex, task ->
                                    val taskExists = tourExists && task.uid != null

                                    val entryRecord = if (taskExists)
                                        entryRecords.first { it.uid == UUID.fromString(task.uid) }
                                    else
                                        TadTourEntry().also { r ->
                                            r.tourUid = tourRecord.uid
                                            r.uid = UUID.randomUUID()
                                        }

                                    entryRecord.also { r ->
                                        r.position = newPosition
                                        r.orderId = task.orderId
                                        r.orderTaskType = when (task.taskType) {
                                            Task.Type.DELIVERY -> TaskType.DELIVERY.value
                                            Task.Type.PICKUP -> TaskType.DELIVERY.value
                                        }

                                        // Set appointments with replaced tour date
                                        r.appointmentFrom = task.appointmentStart?.replaceDate(date)?.toTimestamp()
                                        r.appointmentTo = task.appointmentEnd?.replaceDate(date)?.toTimestamp()

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
                                        task.uid = entryRecord.uid.toString()
                                    }
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

    fun put(tour: Tour): Tour {
        return this.put(listOf(tour)).first()
    }

    //region REST
    /**
     * Get tours
     * @throws NoSuchElementException When no tours have been found
     */
    override fun get(
            ids: List<Long>?,
            stationNo: Long?,
            userId: Long?,
            from: ShortDate?,
            to: ShortDate?,
            isMobile: Boolean?,

            overview: Boolean
    ): List<Tour> {
        val tourRecords =
                this.tourRepo.findAll(BooleanBuilder()
                        .let {
                            if (ids?.count() ?: 0 > 0)
                                it.and(tadTour.id.`in`(ids))
                            else
                                it
                        }
                        .letWithNotNull(stationNo, { and(tadTour.stationNo.eq(it)) })
                        .letWithNotNull(userId, { and(tadTour.userId.eq(it)) })
                        .letWithNotNull(from, { and(tadTour.date.goe(it.toString())) })
                        .letWithNotNull(to, { and(tadTour.date.loe(it.toString())) })
                        .letWithNotNull(isMobile, {
                            if (it)
                                and(tadTour.nodeUid.isNotNull)
                            else
                                and(tadTour.nodeUid.isNull)
                        })
                )
                        .also {
                            if (it.count() == 0) {
                                // No tour records in selection
                                throw NoSuchElementException()
                            }
                        }

        // Pre-fetch relevant records
        val tourEntries =
                if (!overview)
                    tourEntryRepo
                            .findAll(tadTourEntry.tourUid.`in`(tourRecords.map { it.uid }))
                else listOf()

        val orders =
                if (!overview)
                    this.orderService.get(ids = tourEntries
                            .map { it.orderId }
                            .distinct())
                else
                    listOf()

        return tourRecords.map {
            it.toTour(
                    orderRecordsById = orders.associateBy { it.id },
                    tourEntryRecordsByTourId = tourEntries.groupBy { it.tourUid }
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

        return tourRecord.toTour()
    }

    /**
     * Get tour by uid
     */
    override fun getByUid(uid: String): Tour {
        val tourRecord = tourRepo.findOne(tadTour.uid.eq(UUID.fromString(uid)))
                .toNullable()
                ?: throw NoSuchElementException("No tour with uid [${uid}]")

        return tourRecord.toTour()
    }

    /**
     * Get tour by node
     */
    override fun getByNode(nodeUid: String): Tour {
        val nodeUuid = UUID.fromString(nodeUid)

        val tourRecord = tourRepo
                .findOne(tadTour.nodeUid.eq(nodeUuid))
                .toNullable()
                ?: throw NoSuchElementException()

        return tourRecord.toTour()
    }

    /**
     * Get the (current) tour for a user
     */
    override fun getByUser(userId: Long): Tour {
        // Get latest tour for user
        val tourRecord = tourRepo.findMostRecentByUserId(userId)
                ?: throw NoSuchElementException("No assignable tour for user [${userId}]")

        return tourRecord.toTour()
    }

    /**
     * Get by custom id
     * @param customId custom id
     */
    override fun getByCustomId(customId: String): Tour {
        return this.emf.withEntityManager { em ->
            val tourRecord = em.from(tadTour)
                    .where(tadTour.customId.eq(customId))
                    .fetchFirst()
                    ?: throw NoSuchElementException("No tour with custom id [${customId}]")

            tourRecord.toTour()
        }
    }

    override fun delete(
            ids: List<Long>?,
            userId: Long?,
            stationNo: Long?,
            customIds: List<String>?,
            includeRelated: Boolean?) {

        // Prevent deletion of all records, as jax-rs/resteasy doesn't make a difference
        // between empty and no list on query collection params
        if (ids?.count() == 0 &&
                userId == null &&
                stationNo == null &&
                customIds?.count() == 0)
            return

        emf.transaction { em ->
            em.from(tadTour)
                    .select(tadTour.id, tadTour.stationNo, tadTour.uid)
                    .where(BooleanBuilder()
                            .letWithItems(ids, {
                                and(tadTour.id.`in`(it))
                            })
                            .letWithNotNull(userId, {
                                and(tadTour.userId.eq(it))
                            })
                            .letWithNotNull(stationNo, {
                                and(tadTour.stationNo.eq(it))
                            })
                            .letWithItems(customIds, {
                                and(tadTour.customId.`in`(it))
                            })
                    )
                    .fetch()
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
                            val tourUids = records
                                    .map { it.get(tadTour.uid) }
                                    .distinct()

                            em.delete(
                                    tadTourEntry,
                                    tadTourEntry.tourUid.`in`(tourUids))

                            em.delete(
                                    tadTour,
                                    tadTour.uid.`in`(tourUids)
                            )

                            try {
                                this.smartlane.deleteRoutes(
                                        tours = records.map {
                                            Tour(
                                                    id = it.get(tadTour.id),
                                                    uid = it.get(tadTour.uid).toString(),
                                                    stationNo = it.get(tadTour.stationNo)
                                            )
                                        }
                                )
                                        .subscribeBy(onError = { e -> log.error(e.message, e) })
                            } catch (e: Throwable) {
                                log.error("Failed to delegate deletion for tours [${tourUids}]: ${e.message}")
                            }
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
            response: AsyncResponse?) {

        @Suppress("NAME_SHADOWING")
        val response = response!!

        data class Optimization(val tourId: Long, val result: List<Tour>)

        log.info { "Starting ruote optimization for tour(s) [${ids.joinToString(", ")}] " }

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
                                // Vehicles provided -> split optimization

                                // Reset ids for all optimized tours, enforcing creation
                                tours.forEach { it.id = null }

                                // Create tours from optimized results
                                this.put(tours).also {
                                    // Update custom ids @smartlane
                                    this.smartlane.updateRoutes(it)
                                            .subscribeBy(onError = { e -> log.error(e.message, e) })
                                }

                            } else {
                                // In-place optimization of single tour
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
                                response.resume(e)
                            }
                        }
                )


        if (!waitForCompletion) {
            response.resume(Response
                    .status(Response.Status.ACCEPTED)
                    .build()
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
            response: AsyncResponse?) {

        this.optimize(
                ids = listOf(id),
                waitForCompletion = waitForCompletion,
                options = options,
                response = response!!)
    }


    override fun status(stationNo: Long, sink: SseEventSink, sse: Sse) {

        sink.push(
                sse = sse,
                events = this.optimizations.updated
                        .filter {
                            this.emf.withEntityManager { em ->
                                em.from(tadTour)
                                        .select(tadTour.id)
                                        .where(tadTour.stationNo.eq(stationNo))
                                        .fetch()
                                        .contains(it.id)
                            }
                        },
                onError = { log.error(it.message) }
        )

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
        val node = this.nodeService.getByUid(nodeUid)

        /** Handle error response on message based requests (nodeRequestUid was provided) */
        fun handleErrorResponse(error: TourOptimizationResult.ErrorType) {
            // Only send error response on node requests
            if (nodeRequestUid != null) {
                JmsEndpoints.node.topic(identityUid = Identity.Uid(node.uid))
                        .channel()
                        .send(TourOptimizationResult(
                                requestUid = nodeRequestUid,
                                nodeUid = node.uid,
                                error = error
                        ))
            }
        }

        try {
            if (options.vehicles?.count() ?: 0 > 1)
                throw IllegalArgumentException("Multiple vehicles are not supported when " +
                        "optimizing a single (node related) tour")

            val tourRecord = tourRepo.findOne(tadTour.nodeUid.eq(UUID.fromString(nodeUid)))
                    .toNullable()
                    ?: throw NoSuchElementException("No tour for node [${nodeUid}]")

            val tour = this.getById(tourRecord.id.toLong())

            val userId = tour.userId?.toInt()
                    ?: throw IllegalArgumentException("Node tour is missing user id")

            val user = this.userService.getById(userId)

            // Update smartlane driver
            this.smartlane.putDriver(
                    user = user
            )
                    .blockingAwait()

            this.optimize(
                    tour = tour,
                    options = options
            )
                    .subscribeBy(
                            onSuccess = { tours ->
                                val optimizedTour = tours.first()

                                JmsEndpoints.node.topic(identityUid = Identity.Uid(node.uid))
                                        .channel()
                                        .send(TourOptimizationResult(
                                                requestUid = nodeRequestUid,
                                                nodeUid = node.uid,
                                                tour = optimizedTour
                                        ))

                                this.smartlane.assignDriver(
                                        user = user,
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

    override fun subscribe(stationNo: Long, sink: SseEventSink, sse: Sse) {
        this.emf.withEntityManager { _ ->
            sink.push(
                    sse = sse,
                    events = this.updated
                            .filter {
                                it.stationNo == stationNo
                            },
                    onError = { log.error(it.message) }
            )
        }
    }

    override fun smartlaneClean() {
        this.smartlane.clean()
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
            TourOptimizationStatus(
                    id = id,
                    inProgress = true
            )
                    .also { status ->
                        this.byId.set(id, status)
                        this.updatedSubject.onNext(status)
                    }
        }

        /**
         * Should be called when tour optimization finishes
         * @param tourRecord Tour record
         */
        fun onFinish(id: Long, success: Boolean? = null) {
            TourOptimizationStatus(
                    id = id,
                    inProgress = false,
                    success = success
            )
                    .also { status ->
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
    private fun optimize(
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
                val station = this.stationRepo.findByStationNo(stationNo.toInt())
                        ?: throw NoSuchElementException("Station no [${stationNo}] doesn't exist")

                options.start = station.toAddress()
            }
        }

        return this.smartlane.optimize(
                tour = tour,
                options = options
        )
                .doOnSubscribe {
                    this.optimizations.onStart(tourId)
                }
                .doOnError {
                    this.optimizations.onFinish(tourId, success = false)
                }
                .doOnSuccess {
                    this.optimizations.onFinish(tourId, success = true)
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
            val station = stationRepo.findByStationNo(stationNo)
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

        // Set tour date to message timestamp (nodes may not provide this)
        tour.date = ShortDate(message.timestamp)

        // Update tour
        this.put(listOf(tour))

    }
    //endregion

    //region Transformations
    private fun TadTour.toTour(
            orderRecordsById: Map<Long, org.deku.leoz.service.internal.OrderService.Order>? = null,
            tourEntryRecordsByTourId: Map<UUID, List<TadTourEntry>>? = null): Tour {

        val tourRecord = this

        // Fetch tour entries. Equal positions represent stop tasks in order of PK
        val tourEntryRecords = if (tourEntryRecordsByTourId != null)
            tourEntryRecordsByTourId.get(
                    tourRecord.uid
            )
                    ?.sortedBy { it.position }
                    ?.sortedWith(compareBy<TadTourEntry> { it.position }.thenBy { it.id })
                    ?: listOf()
        else {
            tourEntryRepo.findAll(
                    tadTourEntry.tourUid.eq(tourRecord.uid),
                    tadTourEntry.position.asc(),
                    tadTourEntry.id.asc()
            )
        }

        // Order lookup table by id
        val ordersById = if (orderRecordsById != null)
            orderRecordsById
        else
            this@TourServiceV1.orderService.get(ids = tourEntryRecords.map { it.orderId }.distinct())
                    .associateBy { it.id }

        // Gather orders for this tour
        val orders = tourEntryRecords.map { it.orderId }.distinct()
                .map { ordersById.getValue(it) }

        val stops = tourEntryRecords.groupBy { it.position }
                .map { stop ->
                    val tasks = stop.value.map { task ->
                        val taskType = TaskType.valueMap.getValue(task.orderTaskType)

                        Task(
                                id = task.id,
                                uid = task.uid.toString(),
                                orderId = task.orderId,
                                appointmentStart = task.appointmentFrom,
                                appointmentEnd = task.appointmentTo,
                                taskType = when (taskType) {
                                    TaskType.DELIVERY -> Task.Type.DELIVERY
                                    TaskType.PICKUP -> Task.Type.PICKUP
                                }
                        )
                    }

                    val stopParcels = tasks
                            .map { ordersById.getValue(it.orderId) }
                            .flatMap { it.parcels }

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
                            weight = stopParcels.sumByDouble { it.dimension.weight },
                            parcelNumbers = stopParcels.map { it.number },
                            route = stop.value.first().routeMeta?.let {
                                this@TourServiceV1.objectMapper.readValue(it, TourStopRouteMeta::class.java)
                            }
                    )
                }

        return Tour(
                id = tourRecord.id,
                uid = tourRecord.uid.toString(),
                nodeUid = tourRecord.nodeUid?.toString(),
                userId = tourRecord.userId,
                stationNo = tourRecord.stationNo,
                parentId = tourRecord.parentId,
                customId = tourRecord.customId,
                created = tourRecord.created,
                date = ShortDate(tourRecord.date),
                vehicleType = VehicleType.valueMap.get(tourRecord.vehicleType),
                optimized = tourRecord.optimized,
                orders = orders.let { if (it.count() > 0) it else null },
                route = tourRecord.routeMeta?.let {
                    objectMapper.readValue(it, TourRouteMeta::class.java)
                },
                stops = stops.let { if (it.count() > 0) it else null }
        )
    }
    //endregion
}