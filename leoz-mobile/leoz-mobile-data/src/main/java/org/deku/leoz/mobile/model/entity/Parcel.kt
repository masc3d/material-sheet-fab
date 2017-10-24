package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import org.deku.leoz.model.EventNotDeliveredReason
import sx.android.databinding.BaseRxObservable
import sx.io.serialization.Serializable
import sx.io.serialization.Serializer
import java.util.*

/**
 * Mobile parcel entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "parcel")
abstract class Parcel : BaseRxObservable(), Persistable, Observable {
    companion object {
        init {
            // Serializable must be registered here
            Serializer.types.register(Parcel.DamagedInfo::class.java)
        }
    }

    enum class State {
        /** Parcel is pending to be loaded */
        PENDING,
        /** Parcel has been loaded */
        LOADED,
        /** Parcel could not be loaded as it was missing */
        MISSING,
        /** Parcel has been delivered */
        DELIVERED
    }

    @Serializable(0x53d7a72bd9aee6)
    class DamagedInfo(
            /** Picture file uid */
            var pictureFileUid: UUID? = null
    )

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
    @get:Index("parcel_state_index")
    abstract var state: State

    @get:Bindable
    @get:Index
    abstract var reason: EventNotDeliveredReason?

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

    val loadingStateProperty by lazy { ObservableRxField<State>(BR.state, { this.state }) }
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
        it.state = Parcel.State.PENDING
        it.isDamaged = false
        it.modificationTime = Date()
    }
}

/**
 * Mobile parcel metadata entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "parcel_meta")
abstract class ParcelMeta : Meta() {
    companion object {}
    @get:Lazy
    @get:Column(nullable = false)
    @get:ManyToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var parcel: Parcel
}

fun ParcelMeta.Companion.create(
        value: Any
): ParcelMeta {
    return ParcelMetaEntity().also {
        it.set(value)
    }
}