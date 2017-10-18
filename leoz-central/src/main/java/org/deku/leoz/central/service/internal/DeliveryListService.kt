package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.repository.DeliveryListJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.config.Rest
import sx.rs.DefaultProblem
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
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
class DeliveryListService : DeliveryListService {

    @Context
    private lateinit var httpHeaders: HttpHeaders

    @Inject
    private lateinit var deliveryListRepository: DeliveryListJooqRepository

    @Inject
    private lateinit var orderService: org.deku.leoz.central.service.internal.OrderService

    @Inject
    private lateinit var userRepository: UserJooqRepository

    override fun getById(id: Long): org.deku.leoz.service.internal.DeliveryListService.DeliveryList {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
                ?: throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        val deliveryList: DeliveryListService.DeliveryList
        val deliveryListRecord: TadVDeliverylistRecord?

        deliveryListRecord = this.deliveryListRepository.findById(id)
                ?:
                throw DefaultProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)
        when {
            deliveryListRecord.debitorId.toInt() != authorizedUserRecord.debitorId
//                depotRepository.findDebitorDepots(authorizedUserRecord.debitorId).map { (deliveryListRecord.deliveryStation.toInt()) }.isEmpty()
            ->
                throw DefaultProblem(status = Response.Status.FORBIDDEN)
        }

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
                                            stopType = when (dlDetailsRecord.stoptype) {
                                                "DELIVERY" -> DeliveryListService.Task.Type.DELIVERY
                                                "PICKUP" -> DeliveryListService.Task.Type.PICKUP
                                                else -> throw UnsupportedOperationException()
                                            }
                                    )
                            ))
                }

        return deliveryList
    }

    override fun get(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
                ?: throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
                ?: throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

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
}