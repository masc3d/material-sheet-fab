package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.gojuno.koptional.None
import com.gojuno.koptional.toOptional
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.rx.ObservableRxProperty
import sx.rx.behave
import sx.rx.toSingletonObservable
import sx.time.TimeSpan
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stop view model
 * Created by masc on 26.06.17.
 */
class StopViewModel(
        val stop: Stop,
        val timerEvent: Observable<Unit>,
        val isStateVisible: Boolean = false
) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val context: Context by Kodein.global.lazy.instance()

    private val editModeProperty = ObservableRxProperty(false)
    var editMode by editModeProperty

    var editModeField = this.editModeProperty.map { it.value }.toField()

    /**
     * Merge tick event with a static, so it ticks once initially to avoid deferred rendering
     */
    private val tickEvent = Observable.merge(
            Observable.just(Unit),
            timerEvent
    )

    val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val address = AddressViewModel(stop.address)

    val addressLine2Visible by lazy {
        Observable.combineLatest(
                this.editModeProperty.map { it.value },
                Observable.just(address.hasAddressLine2),
                BiFunction { editMode: Boolean, hasAddressLine2: Boolean ->
                    !editMode && hasAddressLine2
                }
        )
                .toField()
    }

    val addressLine3Visible by lazy {
        Observable.combineLatest(
                this.editModeProperty.map { it.value },
                Observable.just(address.hasAddressLine3),
                BiFunction { editMode: Boolean, hasAddressLine2: Boolean ->
                    !editMode && hasAddressLine2
                }
        )
                .toField()
    }

    /** The actual appointment date is always displayed as today */
    private val appointmentDate by lazy { Date() }

    private val appointmentFromDate by lazy {
        stop.appointmentStartDate
    }

    private val appointmentToDate by lazy {
        stop.appointmentEndDate
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

    val isStatusBarVisible by lazy {
        this.editModeProperty.map { !it.value }
                .toField()
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
        Observable.combineLatest(
                this.editModeProperty.map { it.value },
                this.stop.stateProperty.map { it.value == Stop.State.PENDING },
                countdownTimespan.map { stop.appointmentState != AppointmentState.NONE },
                io.reactivex.functions.Function3 { editMode: Boolean, stopState: Boolean, appointmentState: Boolean ->
                    !editMode && stopState && appointmentState
                }
        )

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

    /** Stop parcels (observable fireing when stop state changes) */
    private val parcels by lazy {
        Observable.combineLatest(
                stop.tasks
                        .map { it.order }
                        .flatMap { it.parcels }
                        .toSingletonObservable(),
                this.stop.stateProperty.map { it.value },
                BiFunction { parcels: List<Parcel>, state: Stop.State ->
                    parcels
                }
        )
    }

    /** Stop level event reason */
    private val reason by lazy {
        this.parcels.map {
            it.groupBy { it.reason }
                .keys
                .let {
                    if (it.count() > 1)
                        None
                    else
                        it.first().toOptional()
                }
        }
    }

    val modificationTime: ObservableField<String> by lazy {
        this.stop.modificationTimeProperty
                .map {
                    it.value?.let {
                        SimpleDateFormat("dd.mm.yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: ""
                }
                .toField()
    }

    /** Stop state tag text */
    val tagText: ObservableField<String> by lazy {
        stop.stateProperty.map {
            when (it.value) {
                Stop.State.PENDING -> context.getString(R.string.pending)
                Stop.State.CLOSED -> {
                    this.reason.blockingFirst().toNullable().let {
                        if (it != null)
                            it.mobile.textOrName(context)
                        else
                            context.getString(R.string.delivered)
                    }
                }
                else -> ""
            }
        }.toField()
    }

    /** Stop state tag color */
    val tagColor: ObservableField<Int> by lazy {
        stop.stateProperty.map {
            when (stop.state) {
                Stop.State.PENDING -> R.color.colorWhiteSmoke
                else -> {
                    if (this.parcels.blockingFirst().all { it.state == Parcel.State.DELIVERED })
                        R.color.colorGreenTransparent
                    else
                        R.color.colorAccentTransparent
                }
            }
        }.toField()
    }

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback?) {
        super.addOnPropertyChangedCallback(callback)
    }
}