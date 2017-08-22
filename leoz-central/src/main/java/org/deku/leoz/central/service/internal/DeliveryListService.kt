package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistinfoRecord
import org.deku.leoz.central.data.repository.DeliveryListJooqRepository
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Created by JT on 12.07.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/deliverylist")
class DeliveryListService : DeliveryListService {
    @Inject
    private lateinit var deliveryListRepository: DeliveryListJooqRepository

    @Inject
    private lateinit var orderService: org.deku.leoz.central.service.internal.OrderService

    override fun getById(id: Long): DeliveryListService.DeliveryList {
        val deliveryList: DeliveryListService.DeliveryList
        val deliveryListInfo: TadVDeliverylistRecord?

        deliveryListInfo = this.deliveryListRepository.findById(id)
        deliveryList = deliveryListInfo
                ?.toDeliveryList()
                ?:
                throw DefaultProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)

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
        val dlInfos: List<TadVDeliverylistinfoRecord>
        val listInfos = mutableListOf<DeliveryListService.DeliveryListInfo>()

        when {
            deliveryDate != null -> {
                dlInfos = deliveryListRepository.findInfoByDate(deliveryDate.date)
                dlInfos.forEach { dl ->
                    val di: DeliveryListService.DeliveryListInfo = DeliveryListService.DeliveryListInfo(
                            dl.id.toLong(),
                            ShortDate(dl.deliveryListDate))
                    listInfos.add(di)
                }
            }
            else -> {
                TODO("Handle other query types here")
            }
        }
        return listInfos.toList()
    }

    fun TadVDeliverylistRecord.toDeliveryList(): DeliveryListService.DeliveryList {
        val r = this

        val l = DeliveryListService.DeliveryList(
                id = r.id.toLong(),
                info = DeliveryListService.DeliveryListInfo(
                        id = r.id.toLong(),
                        date = ShortDate(r.deliveryListDate)
                ),
                orders = listOf(),
                stops = listOf()
        )
        return l
    }
}