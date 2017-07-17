package org.deku.leoz.mobile.model.requery

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.Parcel

/**
 * Parcel entity
 * Created by masc on 16.07.17.
 */
@Entity(name = "ParcelEntity")
@Table(name = "parcel")
interface IParcel : Persistable, Observable {
    @get:Key @get:Generated
    var id: Int
    var number: String
    var length: Double
    var height: Double
    var width: Double
    var weight: Double
    var state: Parcel.State

    @get:ManyToOne @get:Column(name = "`order`")
    var order: IOrder
}