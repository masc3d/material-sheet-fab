package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.dekuclient.Tables.RKKOPF
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.repository.JooqDeliveryListRepository
import org.deku.leoz.central.data.repository.JooqStationRepository
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.TaskType
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.node.rest.restrictByDebitor
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.TourServiceV1
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.Stopwatch
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.RestProblem
import sx.time.plusDays
import sx.time.toTimestamp
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * Created by JT on 12.07.17.
 */
@Component
//@ApiKey(false) custom API Key Check
@Path("internal/v1/deliverylist")
class DeliveryListService
    :
        org.deku.leoz.service.internal.DeliveryListService,
        MqHandler<DeliveryListService.StopOrderUpdateMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Context
    private lateinit var httpRequest: HttpServletRequest

    @Inject
    private lateinit var deliveryListRepository: JooqDeliveryListRepository

    @Inject
    private lateinit var stationRepository: JooqStationRepository

    @Inject
    private lateinit var orderService: OrderService

    @Inject
    private lateinit var databaseSyncService: DatabaseSyncService

    @Inject
    private lateinit var tourService: org.deku.leoz.central.service.internal.TourServiceV1

    @PostConstruct
    fun onInitialize() {
        this.databaseSyncService.notifications
                .filter { it.tableName == RKKOPF.name }
                .subscribe {
                    try {
                        this.createTours(deliverylistIds = this.deliveryListRepository
                                .findNewerThan(
                                        syncId = it.localSyncId,
                                        // Only handle updates for recent delivery lists
                                        date = Date().plusDays(-1).toTimestamp()
                                ))
                    } catch (e: Throwable) {
                        log.error(e.message, e)
                    }
                }
    }

    override fun createTours(deliverylistIds: List<Long>): List<TourServiceV1.Tour> {
        val dlRecords = deliveryListRepository
                .findByIds(deliverylistIds)

        if (dlRecords.count() == 0)
            return listOf()

        val tours = Stopwatch.createStarted(this,
                "Converting delivery list(s) to tour(s) [${dlRecords.map { it.id.toLong() }.joinToString(", ")}]", {

            val dlDetailRecordsByDlId = deliveryListRepository
                    .findDetailsByIds(deliverylistIds)
                    .groupBy { it.id }


            val ordersById = this.orderService.getByIds(
                    dlDetailRecordsByDlId
                            .flatMap { it.value }
                            .map { it.orderId.toLong() }
            )
                    .associateBy { it.id }

            val tours = dlRecords.map { dlRecord ->
                val dlDetailRecords = dlDetailRecordsByDlId.get(dlRecord.id)
                        ?.sortedBy { it.orderPosition }
                        ?: listOf()

                val orderIds = dlDetailRecords
                        .map { it.orderId.toLong() }

                TourServiceV1.Tour(
                        stationNo = dlRecord.deliveryStation.toLong(),
                        // Tour service has no notion of delivery lists, providing dl ids as custom ids
                        customId = DekuDeliveryListNumber.create(dlRecord.id.toLong()).value,
                        date = ShortDate(dlRecord.deliveryListDate),
                        orders = orderIds.map { ordersById.getValue(it) },
                        stops = dlDetailRecords
                                .groupBy { it.orderPosition }
                                .map { dlStop ->
                                    TourServiceV1.Stop(
                                            tasks = dlStop.value
                                                    .distinctBy { it.orderId.toString() + it.stoptype }
                                                    .map { dlDetailRecord ->
                                                        val orderId = dlDetailRecord.orderId.toLong()
                                                        val order = ordersById.getValue(orderId)

                                                        TourServiceV1.Task(
                                                                orderId = orderId,
                                                                taskType = when (TaskType.valueOf(dlDetailRecord.stoptype)) {
                                                                    TaskType.PICKUP -> TourServiceV1.Task.Type.PICKUP
                                                                    TaskType.DELIVERY -> TourServiceV1.Task.Type.DELIVERY
                                                                },
                                                                appointmentStart = when (TaskType.valueOf(dlDetailRecord.stoptype)) {
                                                                    TaskType.PICKUP -> order.pickupAppointment.dateStart
                                                                    TaskType.DELIVERY -> order.deliveryAppointment.dateStart
                                                                },
                                                                appointmentEnd = when (TaskType.valueOf(dlDetailRecord.stoptype)) {
                                                                    TaskType.PICKUP -> order.pickupAppointment.dateEnd
                                                                    TaskType.DELIVERY -> order.deliveryAppointment.dateEnd
                                                                }
                                                        )
                                                    }

                                    )
                                }
                )

            }

            this.tourService.delete(
                    customIds = tours.mapNotNull { it.customId }
                            // TODO: removal of tours with deprecated custom id. remove in next update.
                            .plus(dlRecords.map { it.id.toLong().toString() })
                            .distinct()
            )

            this.tourService.put(tours)

            tours
        })

        return tours
    }

    override fun getById(id: Long): org.deku.leoz.service.internal.DeliveryListService.DeliveryList {
        val dlRecord: TadVDeliverylistRecord?

        dlRecord = this.deliveryListRepository.findById(id)
                ?: throw RestProblem(
                title = "DeliveryList not found",
                status = Response.Status.NOT_FOUND)

        this.httpRequest.restrictByDebitor { dlRecord.debitorId.toInt() }

        return dlRecord.toDeliveryList()
    }

    override fun getByStationId(stationId: Int?): List<DeliveryListService.DeliveryList> {
        stationId ?: throw RestProblem(status = Status.BAD_REQUEST)

        return this.get(
                stationId = stationId,
                stationNo = null)
    }

    override fun get(stationId: Int?, stationNo: Int?): List<DeliveryListService.DeliveryList> {
        val station = when {
            stationId != null -> stationRepository.findById(stationId)
            stationNo != null -> stationRepository.findByStationNo(stationNo)
            else -> null
        }
                ?: throw NoSuchElementException()

        this.httpRequest.restrictByDebitor { station.debitorId }

        return this.deliveryListRepository
                .findByStationNo(station.depotnr)
                .toDeliveryLists()
    }

    override fun getInfo(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        val authorizedUser = httpRequest.authorizedUser
        val debitorId = authorizedUser.debitorId
                ?: throw IllegalArgumentException("User [${authorizedUser.id}] is missing debitor")

        val dlInfos = when {
            deliveryDate != null -> {
                deliveryListRepository.findInfoByDateDebitorList(
                        deliveryDate = deliveryDate.date,
                        debitorId = debitorId
                )
            }
            else -> {
                deliveryListRepository.findInfoByDebitor(
                        debitorId = debitorId
                )
            }
        }

        return dlInfos
                .sortedWith(compareByDescending<TadVDeliverylistRecord> { it.deliveryListDate }.thenBy { it.id })
                .map {
                    it.toDeliveryListInfo()
                }
    }

    //region Transformations
    fun TadVDeliverylistRecord.toDeliveryListInfo(): DeliveryListService.DeliveryListInfo {
        val r = this

        val l = DeliveryListService.DeliveryListInfo(
                id = r.id.toLong(),
                debitorId = r.debitorId,
                date = ShortDate(r.deliveryListDate),
                modified = r.createDate
        )
        return l
    }

    /**
     * Transform list of delivery list records into service entities in the most efficient manner
     * @return Delivery lists
     */
    fun Iterable<TadVDeliverylistRecord>.toDeliveryLists(): List<DeliveryListService.DeliveryList> {
        val dlDetailRecordsById = this@DeliveryListService.deliveryListRepository
                // The order of detail records are crucial, as they represent stop order
                .findDetailsByIds(this.map { it.id.toLong() })
                .groupBy { it.id }

        val orders = orderService.getByIds(
                dlDetailRecordsById.flatMap { it.value }.map { it.orderId.toLong() }.distinct()
        )
                .associateBy { it.id }

        return this.map { dlRecord ->
            val dlDetailRecords = dlDetailRecordsById.getValue(dlRecord.id)

            val dlDetailRecordsByPosition = dlDetailRecords
                    .groupBy { it.orderPosition }

            DeliveryListService.DeliveryList(
                    id = dlRecord.id.toLong(),
                    info = DeliveryListService.DeliveryListInfo(
                            id = dlRecord.id.toLong(),
                            date = ShortDate(dlRecord.deliveryListDate),
                            debitorId = dlRecord.debitorId,
                            modified = dlRecord.createDate
                    ),
                    orders = dlDetailRecords.map { orders.getValue(it.orderId.toLong()) },
                    stops = dlDetailRecordsByPosition.map { dlDetailsForStop ->
                        DeliveryListService.Stop(
                                tasks = dlDetailsForStop.value
                                        // Filter duplicate tasks
                                        .distinctBy { listOf(it.orderId, it.stoptype) }
                                        .map {
                                            DeliveryListService.Task(
                                                    orderId = it.orderId.toLong(),
                                                    isRemoved = if (it.removedInDeliverylist != 0.0) true else false,
                                                    taskType = DeliveryListService.Task.Type.valueOf(it.stoptype)
                                            ).also {
                                                it.stopType = it.taskType!!
                                            }
                                        }
                        )
                    }
            )
        }
    }

    /**
     * Transform single delivery list
     */
    fun TadVDeliverylistRecord.toDeliveryList(): DeliveryListService.DeliveryList =
            listOf(this).toDeliveryLists().first()

    //endregion

    /**
     * Message handler receiving updated delivery lists
     */
    override fun onMessage(message: DeliveryListService.StopOrderUpdateMessage, replyChannel: MqChannel?) {
        log.trace("Delivery list update received [${message}]")
    }
}