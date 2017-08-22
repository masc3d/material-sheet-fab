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
 * Stop view model
 * Created by masc on 26.06.17.
 */
class StopViewModel(val stop: Stop) : BaseObservable() {

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val address = AddressViewModel(stop.address)

    val appointmentFrom: String
        get() = simpleDateFormat.format(stop.dateStart)

    val appointmentTo: String
        get() = simpleDateFormat.format(stop.dateEnd)

    private val appointmentEndCalendar by lazy {
        stop.dateEnd?.toCalendar()
    }

    val appointmentHour: Int
        get() = appointmentEndCalendar?.get(Calendar.HOUR) ?: 0

    val appointmentMinute: Int
        get() = appointmentEndCalendar?.get(Calendar.MINUTE) ?: 0

    val orderAmount: String
        get() = stop.tasks.map { it.order }.distinct().count().toString()

    val parcelAmount: String
        get() {
            return stop.tasks.flatMap {
                it.order.parcels
            }.count().toString()
        }
}