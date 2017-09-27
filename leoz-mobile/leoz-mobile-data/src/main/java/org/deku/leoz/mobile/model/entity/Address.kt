package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import android.databinding.Bindable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import sx.android.databinding.BaseRxObservable

/**
 * Mobile address entity
 * Created by masc on 16.07.17.
 */
@Entity
@Table(name = "address")
abstract class Address
    : BaseRxObservable(), Persistable, Observable {

    companion object {}

    @get:Key @get:Generated
    abstract val id: Int

    @get:Bindable
    @get:Column(nullable = false)
    abstract var line1: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var line2: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var line3: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var street: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var streetNo: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var zipCode: String
    @get:Bindable
    @get:Column(nullable = false, value = "'DE'")
    abstract var countryCode: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var city: String
    @get:Bindable
    @get:Column(nullable = false)
    abstract var phone: String

    @get:Bindable
    @get:Column(nullable = false)
    abstract var latitude: Double
    @get:Bindable
    @get:Column(nullable = false)
    abstract var longitude: Double

    val line1Field by lazy { ObservableRxField(BR.line1, { this.line1 }) }
}

fun Address.Companion.create(
        line1: String,
        line2: String,
        line3: String,
        street: String,
        streetNo: String,
        zipCode: String,
        countryCode: String = "DE",
        city: String,
        phone: String,

        latitude: Double = 0.0,
        longitude: Double = 0.0): Address {

    return AddressEntity().also {
        it.line1 = line1
        it.line2 = line2
        it.line3 = line3
        it.street = street
        it.streetNo = streetNo
        it.zipCode = zipCode
        it.countryCode = countryCode.toUpperCase()
        it.city = city
        it.phone = phone
        it.latitude = latitude
        it.longitude = longitude
    }
}
