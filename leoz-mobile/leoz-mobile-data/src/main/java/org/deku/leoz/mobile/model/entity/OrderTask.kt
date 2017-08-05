package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.converter.ServiceConverter
import org.deku.leoz.model.ParcelService
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Mobile order task entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_task")
abstract class OrderTask : BaseRxObservable(), Persistable, Observable {

    companion object {}

    enum class TaskType {
        DELIVERY,
        PICKUP
    }

    @get:Key @get:Generated
    abstract val id: Int

    abstract var type: TaskType

    @get:Lazy
    @get:ManyToOne @get:Column(name = "order_")
    abstract var order: Order

    @get:Lazy
    @get:ForeignKey
    @get:OneToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var address: Address
    abstract var appointmentStart: Date?
    abstract var appointmentEnd: Date?
    abstract var isFixedAppointment: Boolean
    abstract var notice: String

    @get:Lazy
    @get:Convert(ServiceConverter::class)
    abstract var services: ArrayList<ParcelService>

    @get:Lazy
    @get:ManyToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var stop: Stop?
}

fun OrderTask.Companion.create(
        type: OrderTask.TaskType,
        address: Address,
        appointmentStart: Date?,
        appointmentEnd: Date?,
        isFixedAppointment: Boolean,
        notice: String,
        services: List<ParcelService>
): OrderTask {
    return OrderTaskEntity().also {
        it.type = type
        it.address = address
        it.appointmentStart = appointmentStart
        it.appointmentEnd = appointmentEnd
        it.isFixedAppointment = isFixedAppointment
        it.notice = notice
        it.services = ArrayList(services)
    }
}