package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Created by masc on 18.07.17.
 */
/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "stop")
abstract class Stop : BaseRxObservable(), Persistable, Observable {

    companion object {}

    enum class State {
        PENDING, DONE, FAILED
    }

    @get:Key @get:Generated
    abstract val id: Int
    abstract var state: State
    @get:OneToMany
    abstract val tasks: MutableList<OrderTask>

    val address by lazy {
        this.tasks.first().address
    }

    val dateStart by lazy {
        this.tasks.first().dateStart ?: Date()
    }

    val dateEnd by lazy {
        this.tasks.first().dateEnd ?: Date()
    }
}

fun Stop.Companion.create(
        state: Stop.State = Stop.State.PENDING,
        stopTasks: List<OrderTask>
): Stop {
    return StopEntity().also {
        it.state = state
        it.tasks.addAll(stopTasks)
    }
}