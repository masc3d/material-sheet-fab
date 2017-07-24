package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "parcel")
abstract class Parcel : BaseRxObservable(), Persistable, Observable {
    companion object {}

    enum class State {
        PENDING, LOADED, MISSING
    }

    enum class DeliveryState {
        PENDING, DELIVERED, NOTDELIVERED
    }

    @get:Key @get:Generated
    abstract val id: Int
    abstract var number: String
    abstract var length: Double
    abstract var height: Double
    abstract var width: Double
    abstract var weight: Double
    @get:Bindable
    abstract var loadingState: State
    @get:Bindable
    abstract var deliveryState: DeliveryState
    @get:Bindable
    abstract var isDamaged: Boolean

    @get:Column(name = "`order`", nullable = true)
    @get:ManyToOne
    abstract var order: Order?


    val loadingStateProperty by lazy { ObservableRxField<State>(BR.loadingState, { this.loadingState }) }
}

fun Parcel.Companion.create(
        number: String,
        length: Double = 0.0,
        height: Double = 0.0,
        width: Double = 0.0,
        weight: Double = 0.0,
        order: Order? = null
): Parcel {
    return ParcelEntity().also {
        it.number = number
        it.length = length
        it.height = height
        it.width = width
        it.weight = weight
        it.loadingState = Parcel.State.PENDING
        it.isDamaged = false
        it.order = order
    }
}