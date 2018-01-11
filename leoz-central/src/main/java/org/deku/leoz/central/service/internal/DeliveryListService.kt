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
import sx.mq.MqChannel
import sx.mq.MqHandler
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
        MqHandler<DeliveryListService.StopOrderUpdateMessage>
{
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
    fun assertOwner(debitorId: Long) {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
                ?: throw RestProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw RestProblem(status = Response.Status.UNAUTHORIZED)

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
        val deliveryList: DeliveryListService.DeliveryList
        val deliveryListRecord: TadVDeliverylistRecord?

        deliveryListRecord = this.deliveryListRepository.findById(id)
                ?:
                throw RestProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)

        this.assertOwner(deliveryListRecord.debitorId)

        deliveryList = deliveryListRecord.toDeliveryList()

        val deliveryListStops = this.deliveryListRepository.findDetailsById(id)
        val orders = orderService.getByIds(deliveryListStops.map { it.orderId.toLong() })
        val deliveryListOrdersById = deliveryListStops
                .groupBy { it.orderId }

        deliveryList.orders = orders
        deliveryList.stops = deliveryList.orders
                .map {
                    val dlDetailsRecord = deliveryListOrdersById.getValue(it.id.toDouble()).first()
                    DeliveryListService.Stop(
                            tasks = listOf(
                                    DeliveryListService.Task(
                                            orderId = it.id,
                                            isRemoved = if (dlDetailsRecord.removedInDeliverylist != 0.0) true else false,
                                            taskType = DeliveryListService.Task.Type.valueOf(dlDetailsRecord.stoptype)
                                    ).also {
                                        it.stopType = it.taskType!!
                                    }
                            ))
                }

        return deliveryList
    }

    override fun get(stationId: Int?): List<DeliveryListService.DeliveryList> {
        stationRepository.
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
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

        return dlInfos.map {
            it.toDeliveryListInfo()
        }
    }

    fun TadVDeliverylistRecord.toDeliveryList(): DeliveryListService.DeliveryList {
        val r = this

        val l = DeliveryListService.DeliveryList(
                id = r.id.toLong(),
                info = DeliveryListService.DeliveryListInfo(
                        id = r.id.toLong(),
                        date = ShortDate(r.deliveryListDate),
                        debitorId = r.debitorId
                ),
                orders = listOf(),
                stops = listOf()
        )
        return l
    }

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
     * Message handler receiving updated delivery lists
     */
    override fun onMessage(message: DeliveryListService.StopOrderUpdateMessage, replyChannel: MqChannel?) {
        log.trace("Delivery list update received [${message}]")
    }
}