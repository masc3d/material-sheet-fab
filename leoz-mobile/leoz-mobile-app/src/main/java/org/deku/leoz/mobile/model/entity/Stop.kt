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
    abstract val stopTasks: MutableList<StopTask>

    val address by lazy {
        this.stopTasks.first().orderTask.address
    }

    val dateStart by lazy {
        this.stopTasks.first().orderTask.dateStart ?: Date()
    }

    val dateEnd by lazy {
        this.stopTasks.first().orderTask.dateEnd ?: Date()
    }
}

fun Stop.Companion.create(
        state: Stop.State = Stop.State.PENDING,
        stopTasks: List<StopTask>
): StopEntity{
    return StopEntity().also {
        it.state = state
        it.stopTasks.addAll(stopTasks)
    }
}