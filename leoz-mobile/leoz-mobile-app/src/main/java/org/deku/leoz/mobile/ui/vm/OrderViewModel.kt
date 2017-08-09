package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import org.deku.leoz.mobile.model.entity.Order

/**
 * Order view model
 * Created by masc on 09.08.17.
 */
class OrderViewModel(val order: Order) : BaseObservable() {

    val id: String
        get() = order.id.toString()

    val senderAddress = AddressViewModel(order.pickupTask.address)

    val parcelAmount: String
        get() = order.parcels.count().toString()
}