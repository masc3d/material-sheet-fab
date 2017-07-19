package org.deku.leoz.mobile.model.service

import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.service.internal.OrderService

// Order service to mobile model conversions

fun OrderService.Order.toOrder(): Order {
    return Order.create(
            //TODO
            id = this.id,
            state = Order.State.PENDING,
            carrier = this.carrier,
            orderClassification = this.orderClassification,
            referenceIDToExchangeOrderID = this.referenceIDToExchangeOrderID,
            deliveryTask = OrderTask.create(
                    type = OrderTask.TaskType.Delivery,
                    address = this.deliveryAddress.toAddress(),
                    notBeforeStart = this.deliveryAppointment.notBeforeStart,
                    dateStart = this.deliveryAppointment.dateStart,
                    dateEnd = this.deliveryAppointment.dateEnd,
                    notice = this.deliveryNotice ?: "",
                    services = this.deliveryServices ?: listOf()
            ),
            pickupTask = OrderTask.create(
                    type = OrderTask.TaskType.Pickup,
                    address = this.pickupAddress.toAddress(),
                    notBeforeStart = this.pickupAppointment.notBeforeStart,
                    dateStart = this.pickupAppointment.dateStart,
                    dateEnd = this.pickupAppointment.dateEnd,
                    notice = this.pickupNotice ?: "",
                    services = this.pickupServices ?: listOf()
            ),
            parcels = this.parcels.map { it.toParcel() }
    )
}

fun OrderService.Order.Address.toAddress(): Address {
    return Address.create(
            line1 = this.line1,
            line2 = this.line2 ?: "",
            line3 = this.line3 ?: "",
            street = this.street,
            streetNo = this.streetNo ?: "",
            zipCode = this.zipCode,
            city = this.city,
            latitude = this.geoLocation?.latitude ?: 0.0,
            longitude = this.geoLocation?.longitude ?: 0.0,
            phone = this.phoneNumber ?: ""
    )
}

fun OrderService.Order.Parcel.toParcel(): Parcel {
    return Parcel.create(
            number = this.number,
            length = this.dimension.length?.toDouble() ?: 0.0,
            height = this.dimension.height?.toDouble() ?: 0.0,
            width = this.dimension.width?.toDouble() ?: 0.0,
            weight = this.dimension.weight ?: 0.0
    )
}