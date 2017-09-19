package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.dateEnd
import org.deku.leoz.mobile.model.entity.dateStart
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.time.TimeSpan
import sx.time.replaceTime
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

    private val context: Context by Kodein.global.lazy.instance()
    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()

    private val tickEvent = Observable.merge(
            Observable.just(Unit),
            timer.tickEvent
    )

    val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val address = AddressViewModel(stop.address)

    /** The actual appointment date is always displayed as today */
    private val appointmentDate by lazy { Date() }

    private val appointmentFromDate by lazy {
        stop.dateStart?.also {
            this.appointmentDate.replaceTime(it)
        }
    }

    private val appointmentToDate by lazy {
        stop.dateEnd?.let {
            this.appointmentDate.replaceTime(it)
        }
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
            ContextCompat.getColor(this.context, R.color.colorRed)
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
        val end: Date? = this.appointmentToDate
        when {
            end != null -> this.tickEvent.map { TimeSpan.between(end, Date()) }
            else -> Observable.empty()
        }
    }

    val isCountdownVisible: ObservableField<Boolean> by lazy {
        countdownTimespan.map { it.totalMinutes < 60 }
                .toField()
    }

    val isCountdownExpired: ObservableField<Boolean> by lazy {
        countdownTimespan.map { it.totalSeconds < 0 }
                .toField()
    }

    val countdownColor: ObservableField<Int> by lazy {
        countdownTimespan.map {
            when {
                it.totalMinutes < 0 -> ContextCompat.getColor(this.context, R.color.colorRed)
                it.totalMinutes <= 30 -> ContextCompat.getColor(this.context, R.color.colorOrange)
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

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM REMOVECALLBACK ${isFixedAppointment}")
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM ADDCALLBACK")
        super.addOnPropertyChangedCallback(callback)
    }
}