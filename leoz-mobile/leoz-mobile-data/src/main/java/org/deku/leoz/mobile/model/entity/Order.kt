package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import com.neovisionaries.i18n.CurrencyCode
import io.requery.*
import org.deku.leoz.model.Carrier
import sx.android.databinding.BaseRxObservable
import sx.io.serialization.Serializable
import sx.io.serialization.Serializer
import java.util.*

/**
 * Mobile order entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_")
abstract class Order : BaseRxObservable(), Persistable, Observable {

    companion object {
        init {
            // Serializable must be registered here
            Serializer.types.register(CashService::class.java)
        }
    }

    @Serializable(name = "CashService")
    data class CashService(
            var cashAmount: Double = 0.0,
            var currency: String = CurrencyCode.EUR.name
    )

    @get:Key
    abstract var id: Long

    @get:Bindable
    @get:Column(nullable = false)
    abstract var carrier: Carrier
    @get:Column(nullable = false)
    abstract var exchangeOrderId: Long

    /** The (optional) delivery list id this order is attached to */
    abstract var deliveryListId: Long?

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val tasks: MutableList<OrderTask>

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val parcels: MutableList<Parcel>

    @get:Lazy
    @get:OneToMany(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract val meta: MutableList<OrderMeta>

    @get:Column
    abstract var creationTime: Date?

    val pickupTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.PICKUP }
    }

    val deliveryTask by lazy {
        this.tasks.first { it.type == OrderTask.TaskType.DELIVERY }
    }
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
        it.creationTime = Date()
    }

    deliveryTask.order = entity
    pickupTask.order = entity

    return entity
}

/**
 * Mobile order metadata entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_meta")
abstract class OrderMeta : Meta() {
    companion object {}

    @get:Lazy
    @get:Column(name = "order_", nullable = false)
    @get:ManyToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var order: Order
}

fun OrderMeta.Companion.create(
        value: Any
): OrderMeta {
    return OrderMetaEntity().also {
        it.set(value)
    }
}