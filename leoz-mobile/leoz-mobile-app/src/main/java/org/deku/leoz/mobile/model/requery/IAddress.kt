package org.deku.leoz.mobile.model.requery

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*

/**
 * Created by masc on 16.07.17.
 */
@Entity(name = "AddressEntity")
@Table(name = "address")
interface IAddress : Persistable, Observable {
    @get:Key
    @get:Generated
    @get:Bindable
    var id: Int
    @get:Bindable
    var line1: String
    @get:Bindable
    var line2: String
    @get:Bindable
    var line3: String
    @get:Bindable
    var street: String
    @get:Bindable
    var streetNo: String
    @get:Bindable
    var zipCode: String
    @get:Bindable
    var city: String
    @get:Bindable
    var latitude: Double
    @get:Bindable
    var longitude: Double
    @get:Bindable
    var phone: String
}