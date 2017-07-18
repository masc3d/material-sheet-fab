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
@Table(name = "stop_task")
abstract class StopTask : BaseRxObservable(), Persistable, Observable {

    companion object {}

    @get:Key @get:Generated
    abstract val id: Int
    @get:ForeignKey @get:OneToOne @get:Column(name = "`order`")
    abstract var order: Order
    @get:ForeignKey @get:OneToOne
    abstract var orderTask: OrderTask

    @get:ManyToOne
    abstract var stop: Stop
}

fun StopTask.Companion.create(
        order: Order,
        orderTask: OrderTask
): StopTaskEntity{
    return StopTaskEntity().also {
        it.order = order
        it.orderTask = orderTask
    }
}