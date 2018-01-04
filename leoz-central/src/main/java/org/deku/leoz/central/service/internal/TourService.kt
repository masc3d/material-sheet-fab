package org.deku.leoz.central.service.internal

import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstNodeRecord
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR_ENTRY
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourRecord
import org.deku.leoz.central.data.repository.fetchById
import org.deku.leoz.central.data.repository.fetchByTourId
import org.deku.leoz.central.data.repository.fetchByUid
import org.deku.leoz.central.data.repository.uid
import org.deku.leoz.model.TaskType
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.entity.Address
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
import sx.rs.DefaultProblem
import sx.rx.subscribeOn
import sx.time.plusDays
import sx.time.plusHours
import sx.time.replaceDate
import sx.time.toTimestamp
import java.util.*
import java.util.concurrent.ExecutorService
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
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var smartlaneBridge: SmartlaneBridge

    //region REST

    /**
     * Get tour by id
     */
    override fun getById(id: Int): TourServiceV1.Tour {
        val tourRecord = dsl.fetchOne(TAD_TOUR, TAD_TOUR.ID.eq(id)) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        val nodeRecord = dsl.selectFrom(MST_NODE)
                .fetchById(tourRecord.nodeId) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "No node uid for id [${tourRecord.nodeId}]"
                )

        return tourRecord.toTour(nodeRecord)
    }

    /**
     * Get tour by node
     */
    override fun getByNode(nodeUid: String): TourServiceV1.Tour {
        val nodeRecord = dsl.selectFrom(MST_NODE).fetchByUid(nodeUid) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "Unknown node uid ${nodeUid}"
                )

        val tourRecord = dsl.fetchOne(TAD_TOUR, TAD_TOUR.NODE_ID.eq(nodeRecord.nodeId)) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        return tourRecord.toTour(nodeRecord)
    }

    /**
     * Get tour by user
     */
    override fun getByUser(userId: Int): TourServiceV1.Tour {
        val tourRecord = dsl.fetchOne(
                TAD_TOUR,
                TAD_TOUR.USER_ID.eq(userId).and(TAD_TOUR.NODE_ID.isNull)
        ) ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "No assignable tour for user [${userId}]"
                )

        return tourRecord.toTour()
    }

    /**
     * Optimize tour
     */
    override fun optimize(id: Int, response: AsyncResponse) {
        val tour = this.getById(id)

        this.smartlaneBridge.optimizeRoute(
                tour.toRoutingInput()
        )
                .subscribeBy(
                        onNext = { route ->
                            route.deliveries
                                    .sortedBy { it.orderindex }
                                    .forEachIndexed { index, delivery ->
                                        // TODO: update `tad_tour_entry` positions
                                        val oldIndex = tour.stops.indexOfFirst { it.id == delivery.customId.toInt() }

                                        log.trace { "ROUTE POS UPDATE ${oldIndex} -> ${index} (${delivery.orderindex})" }
                                    }

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createFromDeliveryList(deliveryListId: Int): TourServiceV1.Tour {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                    it.pdtTo = stop.appointmentEnd?.replaceDate(Date().plusDays(1))?.plusHours(5)
                    log.trace("PDT ${it.pdtFrom} -> ${it.pdtTo}")

                    // Track stop via custom id
                    it.customId = stop.id?.toString()
                }
            }
        }
    }

    /**
     * Transform tour/node record into service entity
     * @param nodeRecord Optional node record referring to this tour. If not provided it will be looked up.
     */
    private fun TadTourRecord.toTour(nodeRecord: MstNodeRecord? = null): TourServiceV1.Tour {
        val tourRecord = this

        if (tourRecord.nodeId != null && nodeRecord == null)
            throw IllegalArgumentException("Inconsistent arguments")

        if (nodeRecord != null && tourRecord.nodeId != nodeRecord.nodeId)
            throw IllegalArgumentException("Inconsistent arguments")

        // Fetch tour entries. Equal positions represent stop tasks in order of PK
        val tourEntryRecords = dsl
                .selectFrom(TAD_TOUR_ENTRY)
                .fetchByTourId(tourRecord.id)

        val orders = this@TourServiceV1.orderService.getByIds(
                tourEntryRecords.map { it.orderId }.distinct()
        )

        val ordersById = mapOf(
                *orders.map { Pair(it.id, it) }.toTypedArray()
        )

        return TourServiceV1.Tour(
                id = tourRecord.id,
                nodeUid = nodeRecord?.uid,
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
                        it.position = index.toDouble()
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
