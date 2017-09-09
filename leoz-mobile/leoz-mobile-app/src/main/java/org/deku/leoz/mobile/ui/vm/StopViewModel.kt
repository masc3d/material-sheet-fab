package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.Observable
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stop view model
 * Created by masc on 26.06.17.
 */
class StopViewModel(
        val stop: Stop) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

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

    val isFixedAppointment: Boolean
        get() = stop.tasks.any { it.isFixedAppointment }

    @get:ColorInt val clockColor: Int
        get() = if (this.isFixedAppointment) Color.RED else Color.GRAY

    val isCountdownVisible: Boolean
        get() = false

    val isClockVisible: Boolean
        get() = when {
            isCountdownVisible || isFixedAppointment -> true
            else -> false
        }

    val orderAmount: String
        get() = stop.tasks.map { it.order }.distinct().count().toString()

    val parcelAmount: String by lazy {
        stop.tasks.flatMap { it.order.parcels }.count().toString()
    }

    val services: List<ParcelService> by lazy {
        stop.tasks
                .flatMap { it.services }
                .distinct()
                .filter { it.mobile.text != null }
    }

    val hasServices: Boolean by lazy {
        this.services.count() > 0
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        log.trace("SVM REMOVECALLBACK ${isFixedAppointment}")
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        log.trace("SVM ADDCALLBACK")
        super.addOnPropertyChangedCallback(callback)
    }
}