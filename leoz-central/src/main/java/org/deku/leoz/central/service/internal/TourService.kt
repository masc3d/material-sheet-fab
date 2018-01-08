package org.deku.leoz.central.service.internal

import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_USER
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
import sx.time.plusDays
import sx.time.plusHours
import sx.time.replaceDate
import sx.time.toTimestamp
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
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
        MqHandler<TourServiceV1.TourUpdate> {

    private val log = LoggerFactory.getLogger(this.javaClass)

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
    fun optimize(id: Int): Completable {
        val tour = this.getById(id)

        return this.smartlaneBridge.optimizeRoute(
                tour.toRoutingInput()
        )
                .doOnNext { route ->
                    dsl.transaction { _ ->
                        val entryRecords = tourRepository.findEntriesById(id)

                        route.deliveries
                                .sortedBy { it.orderindex }
                                .forEachIndexed { index, delivery ->
                                    // Select stop tour entry which matches custom id
                                    val entryRecord = entryRecords.first { it.id == delivery.customId.toInt() }

                                    log.trace { "TOUR ENTRY POSITION UPDATE ${entryRecord.position} -> ${index} (${delivery.orderindex})" }

                                    // Update position for all entries on the same (positional) level
                                    val entryIds = entryRecords
                                            .filter { it.position == entryRecord.position }
                                            .map { it.id }

                                    val timestamp = Date().toTimestamp()
                                    dsl.update(TAD_TOUR_ENTRY)
                                            .set(TAD_TOUR_ENTRY.POSITION, (index + 1).toDouble())
                                            .set(TAD_TOUR_ENTRY.TIMESTAMP, timestamp)
                                            .where(TAD_TOUR_ENTRY.ID.`in`(entryIds))
                                            .execute()
                                }
                    }
                }
                .ignoreElements()
    }

    /**
     * Optimize tour
     * @param id Tour id
     * @param response Asynchronous response
     */
    override fun optimize(id: Int, response: AsyncResponse) {
        this.optimize(id)
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
    }

    override fun optimizeSse(id: Int, domainSink: SseEventSink, sse: Sse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun optimizeForNode(nodeUid: String) {
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

        this.optimize(tour.id)
                .subscribeBy(
                        onComplete = {
                            JmsEndpoints.node.topic(identityUid = Identity.Uid(node.uid))
                                    .channel()
                                    .send(TourServiceV1.TourUpdate(
                                            tour = this.getById(tour.id)
                                    ))
                        },
                        onError = {
                            // TODO send error message
                        }
                )
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

    /**
     * Transform tour into smartlane routing input
     */
    private fun TourServiceV1.Tour.toRoutingInput(): Routinginput {
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

                    // TODO: providing pdt fields causes smartlane to fail (with 500 or `route could not be calculated`)
                    it.pdtFrom = stop.appointmentStart?.replaceDate(Date().plusDays(1))
                    it.pdtTo = stop.appointmentEnd?.replaceDate(Date().plusDays(1))
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


    /**
     * Tour update message handler
     */
    override fun onMessage(message: TourServiceV1.TourUpdate, replyChannel: MqChannel?) {
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
}
