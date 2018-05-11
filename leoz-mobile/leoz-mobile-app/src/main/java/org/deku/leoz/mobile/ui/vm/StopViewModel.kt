package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.annotation.ColorInt
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.gojuno.koptional.None
import com.gojuno.koptional.toOptional
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.combineLatest
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.jetbrains.anko.layoutInflater
import org.slf4j.LoggerFactory
import org.threeten.bp.temporal.ChronoUnit
import sx.android.databinding.toField
import sx.android.databinding.toObservable
import sx.android.getColorCompat
import sx.rx.ObservableRxProperty
import sx.rx.just
import sx.time.TimeSpan
import sx.time.threeten.toInstantBp
import sx.time.threeten.toLocalDateTime
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
        listOf(
                this.editModeProperty.map { !it.value },
                Observable.just(address.hasAddressLine2)
        )
                .combineLatest { it.all { it } }
                .toField()
    }

    val addressLine3Visible by lazy {
        listOf(
                this.editModeProperty.map { it.value },
                address.hasAddressLine3.just()
        )
                .combineLatest {
                    it.all { it }
                }
                .toField()
    }

    /** The actual appointment date is always displayed as today */
    private val appointmentDate by lazy { Date() }

    private val appointmentFromDate get() = stop.appointmentStart
    private val appointmentToDate get() = stop.appointmentEnd

    val appointmentFrom: String
        get() = appointmentFromDate?.let { timeFormat.format(it) } ?: ""

    val appointmentTo: String
        get() = appointmentToDate?.let { timeFormat.format(it) } ?: ""

    private val appointmentEndCalendar by lazy {
        this.appointmentToDate?.toCalendar()
    }

    val appointmentHour: Int
        get() = appointmentEndCalendar?.get(Calendar.HOUR) ?: 0

    val appointmentMinute: Int
        get() = appointmentEndCalendar?.get(Calendar.MINUTE) ?: 0

    val isFixedAppointment: Boolean
        get() = stop.tasks.any { it.isFixedAppointment }

    val hasEta by lazy {
        this.stop.etaProperty.map { it.value != null }.toField()
    }

    val etaText by lazy {
        this.stop.etaProperty.map {
            it.value?.let { timeFormat.format(it) } ?: ""
        }.toField()
    }

    val etaColor by lazy {
        val default = context.getColorCompat(R.color.colorGreen)

        this.stop.etaProperty.map {

            val eta = it.value?.toInstantBp() ?: return@map default
            val appointmentTo = this.appointmentToDate?.toInstantBp() ?: return@map default

            val minutes = eta.until(appointmentTo, ChronoUnit.MINUTES)

            when {
                minutes < 0 -> context.getColorCompat(R.color.colorRed)
                minutes <= 15 -> context.getColorCompat(R.color.colorOrange)
                else -> default
            }
        }
                .toField(default)
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

    val isStatusBarVisible by lazy {
        this.editModeProperty.map { !it.value }
                .toField()
    }

    //region Clock
    @get:ColorInt
    val clockColor: Int
        get() = if (this.isFixedAppointment)
            context.getColorCompat(R.color.colorService)
        else
            context.getColorCompat(R.color.colorLightGrey)

    val isClockVisible: ObservableField<Boolean> by lazy {
        when {
            isFixedAppointment -> true.just().toField()
            else -> isCountdownVisible
        }
    }
    //endregion

    //region Countdown
    private val countdownTimespan: Observable<TimeSpan> by lazy {
        when {
            stop.hasAppointment -> this.tickEvent.map {
                stop.appointmentTimeLeft ?: throw IllegalArgumentException()
            }
            else -> Observable.empty()
        }
    }

    val isCountdownVisible: ObservableField<Boolean> by lazy {
        listOf(
                this.editModeProperty.map { !it.value },
                this.stop.stateProperty.map { it.value == Stop.State.PENDING },
                countdownTimespan.map { stop.appointmentState != AppointmentState.NONE }
        )
                .combineLatest { it.all { it } }
                .toField()
    }

    val countdownColor: ObservableField<Int> by lazy {
        countdownTimespan.map {
            when (stop.appointmentState) {
                AppointmentState.OVERDUE -> context.getColorCompat(R.color.colorRed)
                AppointmentState.SOON -> context.getColorCompat(R.color.colorOrange)
                else -> context.getColorCompat(android.R.color.black)
            }
        }
                .toField()
    }

    val countdownText: ObservableField<String> by lazy {
        countdownTimespan.map { it.format(withHours = false, withSeconds = true) }
                .toField()
    }
    //endregion

    val isClockAreaVisible by lazy {
        listOf(
                this.editModeProperty.map { !it.value },
                this.isClockVisible.toObservable(),
                this.isCountdownVisible.toObservable()
        )
                .combineLatest { it.all { it } }
                .toField()
    }

    /** Stop parcels (observable fireing when stop state changes) */
    private val parcels by lazy {
        Observable.combineLatest(
                stop.tasks
                        .map { it.order }
                        .flatMap { it.parcels }
                        .just(),
                this.stop.stateProperty.map { it.value },
                BiFunction { parcels: List<Parcel>, _: Stop.State ->
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

    val closingTime: ObservableField<String> by lazy {
        this.stop.modificationTimeProperty
                .map {
                    if (this.stop.state == Stop.State.CLOSED)
                        it.value?.let {
                            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
                        } ?: ""
                    else ""
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

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
    }
}