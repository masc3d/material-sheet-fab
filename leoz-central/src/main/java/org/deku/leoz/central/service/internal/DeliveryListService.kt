package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
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

    override fun get(deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        val ll: List<DeliveryListService.DeliveryListInfo>
        ll = listOf()
        return ll
    }

    override fun getById(id: Long): DeliveryListService.DeliveryList {
        val deliveryListService: DeliveryListService.DeliveryList
        when {
            id != null -> {
//                deliveryList = this.deliveryListRepository.findById(id)
//                        ?.toDeliveryList()
//                        ?.toDeliveryList()
//                        ?:
                throw DefaultProblem(
                        title = "DeliveryList not found",
                        status = Response.Status.NOT_FOUND)
            }
            else -> {
                TODO("Handle other query types here")
            }
        }
        return deliveryListService
    }
}

fun TadVDeliverylistRecord.toDeliveryList(): DeliveryListService.DeliveryList {
    val r = this
    val l: DeliveryListService.DeliveryList
    l = r.toDeliveryList()
    return l
}
