package org.deku.leoz.mobile.model.service

import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.service.internal.OrderService

// Order service to mobile model conversions

fun OrderService.Order.toOrder(
        deliveryListId: Long? = null
): Order {
    return Order.create(
            //TODO
            id = this.id,
            carrier = this.carrier,
            exchangeOrderId = this.referenceIDToExchangeOrderID,
            deliveryListId = deliveryListId,
            deliveryTask = OrderTask.create(
                    type = OrderTask.TaskType.DELIVERY,
                    address = this.deliveryAddress.toAddress(),
                    isFixedAppointment = this.deliveryAppointment.notBeforeStart,
                    appointmentStart = this.deliveryAppointment.dateStart,
                    appointmentEnd = this.deliveryAppointment.dateEnd,
                    notice = this.deliveryNotice ?: "",
                    services = this.deliveryServices ?: listOf()
            ),
            pickupTask = OrderTask.create(
                    type = OrderTask.TaskType.PICKUP,
                    address = this.pickupAddress.toAddress(),
                    isFixedAppointment = this.pickupAppointment.notBeforeStart,
                    appointmentStart = this.pickupAppointment.dateStart,
                    appointmentEnd = this.pickupAppointment.dateEnd,
                    notice = this.pickupNotice ?: "",
                    services = this.pickupServices ?: listOf()
            ),
            parcels = this.parcels.map { it.toParcel() }
    ).also { order ->
        this.deliveryCashService?.also {
            order.meta.add(OrderMeta.create(
                    Order.CashService(
                            cashAmount = it.cashAmount,
                            currency = it.currency
                    ))
            )
        }
    }
}

fun OrderService.Order.Address.toAddress(): Address {
    return Address.create(
            line1 = this.line1 ?: "",
            line2 = this.line2 ?: "",
            line3 = this.line3 ?: "",
            street = this.street ?: "",
            streetNo = this.streetNo ?: "",
            zipCode = this.zipCode,
            countryCode = this.countryCode,
            city = this.city ?: "",
            latitude = this.geoLocation?.latitude ?: 0.0,
            longitude = this.geoLocation?.longitude ?: 0.0,
            phone = this.phoneNumber ?: ""
    )
}

fun OrderService.Order.Parcel.toParcel(): Parcel {
    return Parcel.create(
            id = this.id,
            number = this.number,
            length = this.dimension.length?.toDouble() ?: 0.0,
            height = this.dimension.height?.toDouble() ?: 0.0,
            width = this.dimension.width?.toDouble() ?: 0.0,
            weight = this.dimension.weight
    ).also {
        it.state = when {
            this.isDelivered -> Parcel.State.DELIVERED
            this.isMissing -> Parcel.State.MISSING
            else -> Parcel.State.PENDING
        }
        it.isDamaged = this.isDamaged
    }
}
