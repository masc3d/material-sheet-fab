package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.converter.ServiceConverter
import org.deku.leoz.model.ParcelService
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_task")
abstract class OrderTask : BaseRxObservable(), Persistable, Observable {

    companion object {}

    enum class TaskType {
        Delivery,
        Pickup
    }

    @get:Key @get:Generated
    abstract val id: Int

    abstract var type: TaskType
    @get:ManyToOne @get:Column(name = "`order`")
    abstract var order: Order
    @get:ForeignKey @get:OneToOne
    abstract var address: Address
    abstract var dateStart: Date?
    abstract var dateEnd: Date?
    abstract var notBeforeStart: Boolean
    abstract var notice: String

    @get:Convert(ServiceConverter::class)
    abstract var services: ArrayList<ParcelService>

    @get:ManyToOne
    abstract var stop: Stop?
}

fun OrderTask.Companion.create(
        type: OrderTask.TaskType,
        address: Address,
        dateStart: Date?,
        dateEnd: Date?,
        notBeforeStart: Boolean,
        notice: String,
        services: List<ParcelService>
): OrderTaskEntity {
    return OrderTaskEntity().also {
        it.type = type
        it.address = address
        it.dateStart = dateStart
        it.dateEnd = dateEnd
        it.notBeforeStart = notBeforeStart
        it.notice = notice
        it.services = ArrayList(services)
    }
}