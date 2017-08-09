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

    enum class LoadingState {
        PENDING, LOADED, MISSING
    }

    enum class DeliveryState {
        PENDING, DELIVERED, UNDELIVERED
    }

    @get:Key
    abstract var id: Long
    @get:Column(nullable = false)
    abstract var number: String
    @get:Column(nullable = false)
    abstract var length: Double
    @get:Column(nullable = false)
    abstract var height: Double
    @get:Column(nullable = false)
    abstract var width: Double
    @get:Column(nullable = false)
    abstract var weight: Double

    @get:Bindable
    @get:Column(nullable = false)
    abstract var isDamaged: Boolean

    @get:Bindable
    @get:Column(nullable = false)
    @get:Index
    abstract var loadingState: LoadingState

    @get:Bindable
    @get:Column(nullable = false)
    @get:Index
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
    @get:Column(name = "order_", nullable = false)
    @get:ManyToOne
    abstract var order: Order

    val loadingStateProperty by lazy { ObservableRxField<LoadingState>(BR.loadingState, { this.loadingState }) }
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
        it.loadingState = Parcel.LoadingState.PENDING
        it.deliveryState = Parcel.DeliveryState.PENDING
        it.isDamaged = false
    }
}