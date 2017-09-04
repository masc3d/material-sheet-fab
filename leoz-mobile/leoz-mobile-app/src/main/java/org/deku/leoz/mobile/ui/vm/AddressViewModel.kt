package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.view.View
import org.deku.leoz.mobile.model.entity.Address
import org.deku.leoz.mobile.model.entity.address

/**
 * Address view model
 * Created by masc on 09.08.17.
 */
class AddressViewModel(val address: Address) : BaseObservable() {

    val line1: String
        get() = this.address.line1

    val line2: String
        get() = this.address.line2

    val street: String
        get() = "${this.address.street} ${this.address.streetNo}"

    val city: String
        get() = "${this.address.zipCode} ${this.address.city}"

    val hasAddressLine2
        get() = !this.address.line2.isBlank()

    val hasPhoneNumber
        get() = !this.address.phone.isBlank()
}