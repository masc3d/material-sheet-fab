package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.Carrier
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_")
abstract class Order : BaseRxObservable(), Persistable, Observable {

    companion object {}

    enum class State {
        PENDING,
        LOADED,
        DONE,
        FAILED
    }

    @get:Key
    abstract var id: Long
    @get:Bindable
    abstract var state: State
    abstract var carrier: Carrier
    abstract var referenceIDToExchangeOrderID: Long

    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val tasks: MutableList<OrderTask>

    val pickupTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.PICKUP }
    }

    val deliveryTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.DELIVERY }
    }

    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val parcels: MutableList<Parcel>
}

fun Order.Companion.create(
        id: Long,
        state: Order.State,
        carrier: Carrier,
        referenceIDToExchangeOrderID: Long,
        pickupTask: OrderTask,
        deliveryTask: OrderTask,
        parcels: List<Parcel>
): Order {
    val entity = OrderEntity().also {
        it.id = id
        it.state = state
        it.carrier = carrier
        it.referenceIDToExchangeOrderID = referenceIDToExchangeOrderID
        deliveryTask.type = OrderTask.TaskType.DELIVERY
        pickupTask.type = OrderTask.TaskType.PICKUP
        it.tasks.add(pickupTask)
        it.tasks.add(deliveryTask)
        it.parcels.addAll(parcels)
    }

    deliveryTask.order = entity
    pickupTask.order = entity

    return entity
}