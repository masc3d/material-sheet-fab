package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "`order`")
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
    abstract var state: State
    abstract var carrier: Carrier
    abstract var referenceIDToExchangeOrderID: Long
    abstract var orderClassification: OrderClassification

    @get:OneToMany
    abstract val tasks: MutableList<OrderTask>

    val pickupTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.Pickup }
    }

    val deliveryTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.Delivery }
    }

    @get:OneToMany
    abstract val parcels: MutableList<Parcel>
}

fun Order.Companion.create(
        id: Long,
        state: Order.State,
        carrier: Carrier,
        referenceIDToExchangeOrderID: Long,
        orderClassification: OrderClassification,
        pickupTask: OrderTask,
        deliveryTask: OrderTask,
        parcels: List<Parcel>
): OrderEntity {
    return OrderEntity().also {
        it.id = id
        it.state = state
        it.carrier = carrier
        it.referenceIDToExchangeOrderID = referenceIDToExchangeOrderID
        it.orderClassification = orderClassification
        it.tasks.add(pickupTask)
        it.tasks.add(deliveryTask)
        it.parcels.addAll(parcels)
    }
}