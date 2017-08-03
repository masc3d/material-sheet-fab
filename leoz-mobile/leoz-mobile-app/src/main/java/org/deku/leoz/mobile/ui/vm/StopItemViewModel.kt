package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.dateEnd
import org.deku.leoz.mobile.model.entity.dateStart
import org.deku.leoz.mobile.model.entity.OrderTask
import org.deku.leoz.mobile.model.entity.Stop
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by masc on 26.06.17.
 */
class StopItemViewModel(val stop: Stop) : BaseObservable() {

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val line1: String
        get() = this.stop.address.line1

    val line2: String
        get() = this.stop.address.line2

    val street: String
        get() = "${this.stop.address.street} ${this.stop.address.streetNo}"

    val city: String
        get() = "${this.stop.address.zipCode} ${this.stop.address.city}"

    val appointmentFrom: String
        get() = simpleDateFormat.format(stop.dateStart)

    val appointmentTo: String
        get() = simpleDateFormat.format(stop.dateEnd)

    val appointmentHour: Int
        get() = stop.dateStart?.toCalendar()?.get(Calendar.HOUR) ?: 0

    val appointmentMinute: Int
        get() = stop.dateEnd?.toCalendar()?.get(Calendar.MINUTE) ?: 0

    val orderCount: String
        get() = stop.tasks.map { it.order }.distinct().count().toString()

    val parcelCount: String
        get() {
            return stop.tasks.flatMap {
                it.order.parcels
            }.count().toString()
        }

    val taskTypeIcon: Int
        get() {
            return when(stop.tasks.first().type) {
                OrderTask.TaskType.DELIVERY -> R.drawable.ic_truck_delivery
                OrderTask.TaskType.PICKUP -> R.drawable.ic_truck_pickup
            }
        }
}