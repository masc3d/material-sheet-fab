package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.OrderTask

/**
 * Order view model
 * Created by masc on 09.08.17.
 */
class OrderTaskViewModel(val orderTask: OrderTask) : BaseObservable() {

    val id: String
        get() = orderTask.order.id.toString()

    val senderAddress = AddressViewModel(orderTask.address)

    val parcelAmount: String
        get() = orderTask.order.parcels.count().toString()
}