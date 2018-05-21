package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import org.deku.leoz.model.TourStopRouteMeta
import sx.android.databinding.BaseRxObservable
import sx.io.serialization.Serializable
import sx.io.serialization.Serializer
import java.util.*

/**
 * Mobile stop entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "stop")
abstract class Stop : BaseRxObservable(), Persistable, Observable {

    companion object {
        init {
            // Serializable must be registered here
            Serializer.types.register(TourStopRouteMeta::class.java)
        }
    }

    @Serializable(0xd37a60a1a80ea6)
    class Signature(
            val recipient: String,
            val signatureSvg: String
    )

    enum class State {
        /** Stop has no state */
        NONE,
        /** Stop is pending for delivery */
        PENDING,
        /** Stop is closed */
        CLOSED
    }

    @get:Key
    @get:Generated
    abstract val id: Int

    @get:Bindable
    @get:Column(nullable = false)
    @get:Index("stop_state_index")
    abstract var state: State
    val stateProperty by lazy { ObservableRxField(BR.state, { this.state }) }

    @get:Lazy
    @get:OneToMany
    abstract val tasks: MutableList<OrderTask>

    @get:Index("stop_position_index")
    @get:Column(nullable = false)
    /** Stop position as a decimal. Insertions or position changes require calculation of average */
    abstract var position: Double

    @get:Column
    @get:Bindable
    abstract var eta: Date?
    val etaProperty by lazy { ObservableRxField(BR.eta, { this.eta }) }

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val meta: MutableList<StopMeta>

    @get:Bindable
    @get:Index("stop_modificationtime_index")
    abstract var modificationTime: Date?

    @get:Bindable
    @get:Column(nullable = true)
    abstract var delayInMinutes: Int?

    val modificationTimeProperty by lazy { ObservableRxField(BR.modificationTime, { this.modificationTime }) }

    val delayed: Boolean
        get() = delayInMinutes ?: 0 > 0
}

fun Stop.Companion.create(
        state: Stop.State = Stop.State.NONE,
        tasks: List<OrderTask>,
        position: Double = 0.0
): Stop {
    return StopEntity().also {
        it.state = state
        it.tasks.addAll(tasks)
        it.position = position
    }
}

/**
 * Mobile stop metadata entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "stop_meta")
abstract class StopMeta : Meta() {
    companion object {}

    // TODO: column can't be not null, as requery will always set foreign key to null instead of deleting the reference
    @get:Lazy
    @get:Column
    @get:ManyToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var stop: Stop
}

fun StopMeta.Companion.create(
        value: Any
): StopMeta {
    return StopMetaEntity().also {
        it.set(value)
    }
}