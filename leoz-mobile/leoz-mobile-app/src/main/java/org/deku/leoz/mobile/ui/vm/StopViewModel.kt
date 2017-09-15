package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.graphics.Color
import android.support.annotation.ColorInt
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.dateEnd
import org.deku.leoz.mobile.model.entity.dateStart
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.time.TimeSpan
import sx.time.plusMinutes
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

    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()

    private val tickEvent = Observable.merge(
            Observable.just(Unit),
            timer.tickEvent
    )

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

    @get:ColorInt
    val clockColor: Int
        get() = if (this.isFixedAppointment) Color.RED else Color.GRAY

    private val countdownTimespan: Observable<TimeSpan> by lazy {
        val end: Date? = stop.dateEnd
        when {
            end != null -> this.tickEvent.map {
                TimeSpan.between(end, Date())
            }
            else -> Observable.empty()
        }
    }

    val isCountdownVisible: ObservableField<Boolean> by lazy {
        countdownTimespan.map {
            it.totalMinutes < 60
        }
                .toField()
    }

    val countdownColor: ObservableField<Int> by lazy {
        countdownTimespan.map {
            when {
                it.totalMinutes <= 15 -> Color.RED
                else -> Color.BLACK
            }
        }
                .toField()
    }

    val countdownText: ObservableField<String> by lazy {
        countdownTimespan.map {
            it.format(withSeconds = true)
        }
                .toField()
    }

    val isClockVisible: ObservableField<Boolean> by lazy {
        when {
            isFixedAppointment -> Observable.just(true).toField()
            else -> isCountdownVisible
        }
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

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM REMOVECALLBACK ${isFixedAppointment}")
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        log.trace("SVM ADDCALLBACK")
        super.addOnPropertyChangedCallback(callback)
    }
}