package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistinfoRecord
import org.deku.leoz.central.data.repository.DeliveryListJooqRepository
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

    override fun getById(id: Long): DeliveryListService.DeliveryList {
        val deliveryList: DeliveryListService.DeliveryList
        when {
            id != null -> {
                deliveryList = this.deliveryListRepository.findById(id)
                        ?.toDeliveryList()
                        ?:
                        throw DefaultProblem(
                                title = "DeliveryList not found",
                                status = Response.Status.NOT_FOUND)
            }
            else -> {
                TODO("Handle other query types here")
            }
        }
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
        var orders = mutableListOf<OrderService.Order>()
        val l = DeliveryListService.DeliveryList(
                id = r.id.toLong(),
                info = DeliveryListService.DeliveryListInfo(
                        id = r.id.toLong(),
                        date = ShortDate(r.deliveryListDate)
                ),
                orders = orders,
                stops = listOf()
        )
        return l
    }
}