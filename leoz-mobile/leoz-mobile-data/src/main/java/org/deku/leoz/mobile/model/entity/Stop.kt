package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import sx.android.databinding.BaseRxObservable

/**
 * Mobile stop entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "stop")
abstract class Stop : BaseRxObservable(), Persistable, Observable {

    companion object {}

    enum class State {
        /** Stop has no state */
        NONE,
        /** Stop is pending for delivery */
        PENDING,
        /** Stop is closed */
        CLOSED
    }

    @get:Key @get:Generated
    abstract val id: Int

    @get:Column(nullable = false)
    @get:Index
    abstract var state: State

    @get:Lazy
    @get:OneToMany
    abstract val tasks: MutableList<OrderTask>

    @get:Index
    @get:Column(nullable = false)
    /** Stop position as a decimal. Insertions or position changes require calculation of average */
    abstract var position: Double
}

fun Stop.Companion.create(
        state: Stop.State = Stop.State.NONE,
        tasks: List<OrderTask>,
        position: Double = 0.0
): Stop {
    return StopEntity().also {
        it.state = state
        it.tasks.addAll(tasks)
        it.position = 0.0
    }
}