package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ViewDataBinding
import org.deku.leoz.mobile.model.Stop
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
        get() = simpleDateFormat.format(stop.appointment.dateFrom)

    val appointmentTo: String
        get() = simpleDateFormat.format(stop.appointment.dateTo)

    val appointmentHour: Int
        get() = stop.appointment.dateFrom.toCalendar().get(Calendar.HOUR)

    val appointmentMinute: Int
        get() = stop.appointment.dateFrom.toCalendar().get(Calendar.MINUTE)

    val orderCount: String
        get() = stop.orders.size.toString()

    val parcelCount: String
        get() {
            return stop.orders.map {
                it.parcel.size
            }.sum().toString()
        }
}