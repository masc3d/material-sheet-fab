package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.model.requery.AddressEntity
import org.deku.leoz.mobile.model.requery.IAddress
import sx.android.databinding.DelegatingRxObservable

/**
 * Created by masc on 16.07.17.
 */
class Address(
        val entity: AddressEntity = AddressEntity()
) : DelegatingRxObservable(entity), IAddress by entity {

    constructor(
            id: Int = 0,
            line1: String = "",
            line2: String = "",
            line3: String = "",
            street: String = "",
            streetNo: String = "",
            zipCode: String = "",
            city: String = "",
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            phone: String = ""
    ): this() {

        this.id = id
        this.line1 = line1
        this.line2 = line2
        this.line3 = line3
        this.street = street
        this.streetNo = streetNo
        this.zipCode = zipCode
        this.city = city
        this.latitude = latitude
        this.longitude = longitude
        this.phone = phone
    }

    val line1Field by lazy { ObservableRxField(BR.line1, { this.line1 } ) }
}