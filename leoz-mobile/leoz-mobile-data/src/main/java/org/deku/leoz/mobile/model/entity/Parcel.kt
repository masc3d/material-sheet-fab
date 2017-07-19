package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "parcel")
abstract class Parcel : BaseRxObservable(), Persistable, Observable {

    companion object

    @get:Key @get:Generated
    abstract val id: Int
    abstract var number: String
    abstract var length: Double
    abstract var height: Double
    abstract var width: Double
    abstract var weight: Double
    abstract var state: Parcel.State

    @get:ManyToOne @get:Column(name = "`order`", nullable = true)
    abstract var order: Order?

    enum class State {
        PENDING, LOADED, MISSING, DONE, FAILED
    }
}

fun Parcel.Companion.create(
        number: String,
        length: Double = 0.0,
        height: Double = 0.0,
        width: Double = 0.0,
        weight: Double = 0.0,
        state: Parcel.State = Parcel.State.PENDING,
        order: Order? = null
): ParcelEntity {
    return ParcelEntity().also {
        it.number = number
        it.length = length
        it.height = height
        it.width = width
        it.weight = weight
        it.state = state
        it.order = order
    }
}