package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.time.TimeSpan
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

/**
 * Stop view model
 * Created by masc on 26.06.17.
 */
class StopViewModel(
        val stop: Stop,
        val timerEvent: Observable<Unit>) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val context: Context by Kodein.global.lazy.instance()

    /**
     * Merge tick event with a static, so it ticks once initially to avoid deferred rendering
     */
    private val tickEvent = Observable.merge(
            Observable.just(Unit),
            timerEvent
    )

    val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val address = AddressViewModel(stop.address)

    /** The actual appointment date is always displayed as today */
    private val appointmentDate by lazy { Date() }

    private val appointmentFromDate by lazy {
        stop.appointmentFromDate
    }

    private val appointmentToDate by lazy {
        stop.appointmentToDate
    }

    val appointmentFrom: String
        get() = timeFormat.format(appointmentFromDate)

    val appointmentTo: String
        get() = timeFormat.format(appointmentToDate)

    private val appointmentEndCalendar by lazy {
        this.appointmentToDate?.toCalendar()
    }

    val appointmentHour: Int
        get() = appointmentEndCalendar?.get(Calendar.HOUR) ?: 0

    val appointmentMinute: Int
        get() = appointmentEndCalendar?.get(Calendar.MINUTE) ?: 0

    val isFixedAppointment: Boolean
        get() = stop.tasks.any { it.isFixedAppointment }

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

    //region Clock
    @get:ColorInt
    val clockColor: Int
        get() = if (this.isFixedAppointment)
            ContextCompat.getColor(this.context, R.color.colorService)
        else
            ContextCompat.getColor(this.context, R.color.colorLightGrey)

    val isClockVisible: ObservableField<Boolean> by lazy {
        when {
            isFixedAppointment -> Observable.just(true).toField()
            else -> isCountdownVisible
        }
    }
    //endregion

    //region Countdown
    private val countdownTimespan: Observable<TimeSpan> by lazy {
        when {
            stop.hasAppointment -> this.tickEvent.map { stop.appointmentTimeLeft ?: throw IllegalArgumentException() }
            else -> Observable.empty()
        }
    }

    val isCountdownVisible: ObservableField<Boolean> by lazy {
        countdownTimespan.map { stop.appointmentState != AppointmentState.NONE }
                .toField()
    }

    val isCountdownExpired: ObservableField<Boolean> by lazy {
        countdownTimespan.map { stop.appointmentState == AppointmentState.OVERDUE }
                .toField()
    }

    val countdownColor: ObservableField<Int> by lazy {
        countdownTimespan.map {
            when (stop.appointmentState) {
                AppointmentState.OVERDUE -> ContextCompat.getColor(this.context, R.color.colorRed)
                AppointmentState.SOON -> ContextCompat.getColor(this.context, R.color.colorOrange)
                else -> ContextCompat.getColor(this.context, android.R.color.black)
            }
        }
                .toField()
    }

    val countdownText: ObservableField<String> by lazy {
        countdownTimespan.map { it.format(withHours = false, withSeconds = true) }
                .toField()
    }
    //endregion

    var editMode = ObservableField<Boolean>(false)

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM REMOVECALLBACK")
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM ADDCALLBACK")
        super.addOnPropertyChangedCallback(callback)
    }
}