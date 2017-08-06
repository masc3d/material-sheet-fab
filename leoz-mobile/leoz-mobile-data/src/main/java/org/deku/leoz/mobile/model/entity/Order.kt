package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.Carrier
import sx.android.databinding.BaseRxObservable

/**
 * Mobile order entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_")
abstract class Order : BaseRxObservable(), Persistable, Observable {

    companion object {}

    @get:Key
    abstract var id: Long

    @get:Bindable
    abstract var carrier: Carrier
    abstract var exchangeOrderId: Long

    /** The (optional) delivery list id this order is attached to */
    abstract var deliveryListId: Long?

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val tasks: MutableList<OrderTask>

    val pickupTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.PICKUP }
    }

    val deliveryTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.DELIVERY }
    }

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val parcels: MutableList<Parcel>
}

fun Order.Companion.create(
        id: Long,
        carrier: Carrier,
        exchangeOrderId: Long,
        pickupTask: OrderTask,
        deliveryTask: OrderTask,
        parcels: List<Parcel>,
        deliveryListId: Long? = null
): Order {
    val entity = OrderEntity().also {
        it.id = id
        it.carrier = carrier
        it.exchangeOrderId = exchangeOrderId
        deliveryTask.type = OrderTask.TaskType.DELIVERY
        pickupTask.type = OrderTask.TaskType.PICKUP
        it.tasks.add(pickupTask)
        it.tasks.add(deliveryTask)
        it.parcels.addAll(parcels)
        it.deliveryListId = deliveryListId
    }

    deliveryTask.order = entity
    pickupTask.order = entity

    return entity
}