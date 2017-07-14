package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.TadVDeliverylist
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistDetailsRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistinfoRecord
import org.deku.leoz.central.data.repository.DeliveryListJooqRepository
import org.deku.leoz.central.data.repository.OrderJooqRepository
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
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
    private lateinit var OrderService: org.deku.leoz.central.service.internal.OrderService

    override fun getById(id: Long): DeliveryListService.DeliveryList {
        val deliveryList: DeliveryListService.DeliveryList
        val deliveryListInfo: TadVDeliverylistRecord?
        val orders = mutableListOf<OrderService.Order>()
        val stops = mutableListOf<DeliveryListService.Stop>()
        deliveryListInfo = this.deliveryListRepository.findById(id)
        deliveryList = deliveryListInfo     //this.deliveryListRepository.findById(id)
                ?.toDeliveryList()
                ?:
                throw DefaultProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)
        val deliveryListOrder = this.deliveryListRepository.findDetailsById(id)
        deliveryListOrder.forEach { orderId ->
            val o = OrderService.getById(orderId.orderId.toLong())
            orders.add(o)
            val s = DeliveryListService.Stop(
                    tasks = listOf(
                            DeliveryListService.Task(
                                    orderId = o.id,
                                    stopType = DeliveryListService.Task.Type.Delivery
                            )
                    )
            )
            stops.add(element = s)
        }
        deliveryList.orders = orders.toList()
        deliveryList.stops = stops.toList()
        return deliveryList

    }

    override fun get(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        val deliveryList: List<TadVDeliverylistinfoRecord>
        val listInfos = mutableListOf<DeliveryListService.DeliveryListInfo>()

        when {
            deliveryDate != null -> {
                deliveryList = deliveryListRepository.findInfoByDate(deliveryDate.date)
                deliveryList.forEach { deliveryList ->
                    val di: DeliveryListService.DeliveryListInfo = DeliveryListService.DeliveryListInfo(
                            deliveryList.id.toLong(),
                            ShortDate(deliveryList.deliveryListDate))
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