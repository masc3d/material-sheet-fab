package org.deku.leoz.service.mock

import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService.Order
import java.util.*

/**
 * Created by masc on 18.06.17.
 */
/*

class MockDeliveryListService : DeliveryListService {
    override fun get( deliveryDate: ShortDate?): List<DeliveryListService.DeliveryListInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val info = DeliveryListService.DeliveryListInfo(
            id = 1,
            date = ShortDate(Date())
    )

    override fun getById(id: Long, apiKey: String?): DeliveryListService.DeliveryList {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getById(id: Long): DeliveryListService.DeliveryList {

        val addr = Order.Address(
                line1 = "Prangenberg",
                line2 = "DEK KURIER",
                line3 = "3. Addresszeile",
                street = "Dörrwiese",
                streetNo = "2",
                zipCode = "36286",
                city = "Neuenstein",
                phoneNumber = "+49 6677 9582"
        )

        val appointment = Order.Appointment(
                dateStart = Date(),
                dateEnd = Date()
        )
        val ps: MutableList<ParcelService> = arrayListOf()

        val order = Order(
                id = 1,
                carrier = Carrier.DER_KURIER,
                orderClassification = OrderClassification.PICKUP_DELIVERY,
                pickupAppointment = appointment,
                pickupAddress = addr,
                parcels = listOf(
                        Order.Parcel(id = 1,
                                number = "10000000001"),
                        Order.Parcel(id = 1,
                                number = "10000000002"),
                        Order.Parcel(id = 1,
                                number = "10000000003")
                ),
                deliveryAppointment = appointment,
                deliveryAddress = addr,
                deliveryServices = ps,
                pickupServices = ps
        )

        return DeliveryListService.DeliveryList(
                id = 1,
                info = this.info,
                orders = listOf(order),
                stops = listOf(DeliveryListService.Stop( listOf() ))
        )
    }

//    override fun get(driver: String?): List<DeliveryListService.DeliveryListInfo> {
//        return listOf(info)
//    }
}
*/
