package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.data.requery.AddressEntity
import org.deku.leoz.mobile.data.requery.IAddressEntity

data class Address(val entity: AddressEntity = AddressEntity())
    : IAddressEntity by entity {

    constructor(
            line1: String,
            line2: String,
            line3: String,
            street: String,
            streetNo: String,
            zipCode: String,
            city: String,
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            phone: String
    ): this() {
        this.address1 = line1
        this.address2 = line2
        this.address3 = line3
        this.street = street
        this.streetNo = streetNo
        this.zipCode = zipCode
        this.city = city
        this.latitude = latitude
        this.longitude = longitude
        this.phone = phone
    }
}
