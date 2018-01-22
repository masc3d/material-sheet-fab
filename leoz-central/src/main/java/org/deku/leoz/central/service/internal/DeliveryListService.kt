package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.repository.JooqDeliveryListRepository
import org.deku.leoz.central.data.repository.JooqStationRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import sx.rs.RestProblem
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import org.zalando.problem.Status
import sx.mq.MqChannel
import sx.mq.MqHandler
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

/**
 * Created by JT on 12.07.17.
 */
@Named
//@ApiKey(false) custom API Key Check
@Path("internal/v1/deliverylist")
class DeliveryListService
    :
        DeliveryListService,
        MqHandler<DeliveryListService.StopOrderUpdateMessage> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Context
    private lateinit var httpHeaders: HttpHeaders

    @Inject
    private lateinit var deliveryListRepository: JooqDeliveryListRepository

    @Inject
    private lateinit var stationRepository: JooqStationRepository

    @Inject
    private lateinit var orderService: org.deku.leoz.central.service.internal.OrderService

    @Inject
    private lateinit var userRepository: JooqUserRepository

    /**
     * Asserts that the user (=apiKey) is entitled to access data for this debitor
     * @throws RestProblem if no authorized
     */
    fun assertOwner(debitorId: Int) {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
                ?: throw RestProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?: throw RestProblem(status = Response.Status.UNAUTHORIZED)

        /**
         * If the authorized user is an ADMIN, it is not necessary to check for same debitor id`s
         * ADMIN-User are allowed to access every delivery-list.
         */
        if (UserRole.valueOf(authorizedUserRecord.role) != UserRole.ADMIN) {
            if (debitorId.toInt() != authorizedUserRecord.debitorId)
                throw RestProblem(status = Response.Status.FORBIDDEN)
        }
    }

    override fun getById(id: Long): org.deku.leoz.service.internal.DeliveryListService.DeliveryList {
        val dlRecord: TadVDeliverylistRecord?

        dlRecord = this.deliveryListRepository.findById(id)
                ?: throw RestProblem(
                title = "DeliveryList not found",
                status = Response.Status.NOT_FOUND)

        this.assertOwner(dlRecord.debitorId.toInt())

        return dlRecord.toDeliveryList()
    }

    override fun getByStationId(stationId: Int?): List<DeliveryListService.DeliveryList> {
        stationId ?: throw RestProblem(status = Status.BAD_REQUEST)

        val station = stationRepository.findById(stationId) ?: throw RestProblem(status = Status.NOT_FOUND)

        this.assertOwner(station.debitorId)

        return this.deliveryListRepository
                .findByStationId(stationId)
                .toDeliveryLists()
    }

    override fun getInfo(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
                ?: throw RestProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
                ?: throw RestProblem(status = Response.Status.UNAUTHORIZED)

        val dlInfos = when {
            deliveryDate != null -> {
                deliveryListRepository.findInfoByDateDebitorList(
                        deliveryDate = deliveryDate.date,
                        debitorId = authorizedUserRecord.debitorId
                )
            }
            else -> {
                deliveryListRepository.findInfoByDebitor(
                        debitorId = authorizedUserRecord.debitorId
                )
            }
        }

        return dlInfos
                .sortedWith(compareByDescending<TadVDeliverylistRecord> { it.createDate }.thenBy{ it.id })
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
                date = ShortDate(r.deliveryListDate)
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
                            debitorId = dlRecord.debitorId
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