package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Mobile parcel entity
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

    @get:Key
    abstract var id: Long
    abstract var number: String
    abstract var length: Double
    abstract var height: Double
    abstract var width: Double
    abstract var weight: Double

    @get:Bindable
    abstract var isDamaged: Boolean

    @get:Bindable
    abstract var loadingState: State

    @get:Bindable
    abstract var deliveryState: DeliveryState

    @get:Bindable
    abstract var event: Event?
    @get:Bindable
    abstract var reason: Reason?

    @get:Bindable
    @get:Index
    abstract var modificationTime: Date?

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val meta: MutableList<ParcelMeta>

    @get:Lazy
    @get:Column(name = "order_")
    @get:ManyToOne
    abstract var order: Order

    val loadingStateProperty by lazy { ObservableRxField<State>(BR.loadingState, { this.loadingState }) }
    val modificationTimeProperty by lazy { ObservableRxField<Date?>(BR.modificationTime, { this.modificationTime }) }
}

fun Parcel.Companion.create(
        id: Long,
        number: String,
        length: Double = 0.0,
        height: Double = 0.0,
        width: Double = 0.0,
        weight: Double = 0.0
): Parcel {
    return ParcelEntity().also {
        it.id = id
        it.number = number
        it.length = length
        it.height = height
        it.width = width
        it.weight = weight
        it.loadingState = Parcel.State.PENDING
        it.isDamaged = false
    }
}