package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.repository.DeliveryListJooqRepository
import org.deku.leoz.central.data.repository.OrderJooqRepository
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import sx.rs.auth.ApiKey
import java.util.*
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
                        ?: throw DefaultProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)
            }
            else -> {
                TODO("Handle other query types here")
            }

            //deliveryList.orders = listOf(OrderService.Order())

        }
        return deliveryList
    }

    override fun get(driver: String?): List<DeliveryListService.DeliveryListInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun TadVDeliverylistRecord.toDeliveryList(): DeliveryListService.DeliveryList {
    val d = DeliveryListService.DeliveryList(info = DeliveryListService.DeliveryListInfo(date = ShortDate(Date()), id = 1), orders = listOf())
    d.info.date = ShortDate( this.deliveryListDate)
    d.info.id = this.id.toLong()
    return d
}