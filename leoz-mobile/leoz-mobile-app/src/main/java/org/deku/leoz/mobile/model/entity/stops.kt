package org.deku.leoz.mobile.model.entity

import io.reactivex.Completable
import sx.time.TimeSpan
import sx.time.replaceTime
import java.util.*

/**
 * State of appointment end time
 */
enum class AppointmentState {
    NONE,
    UPCOMING,
    SOON,
    OVERDUE
}

/**
 * Stop address
 * Created by masc on 03.08.17.
 */
val Stop.address: Address
    get() = this.tasks.first().address

val Stop.dateStart: Date?
    get() = this.tasks.map { it.appointmentStart }.filterNotNull().max()

val Stop.dateEnd: Date?
    get() = this.tasks.map { it.appointmentEnd }.filterNotNull().min()

val Stop.appointmentFromDate: Date?
    get() {
        return this.dateStart?.let {
            Date().replaceTime(it)
        }
    }

val Stop.appointmentToDate: Date?
    get() {
        return this.dateEnd?.let {
            Date().replaceTime(it)
        }
    }

val Stop.appointmentTimeLeft: TimeSpan?
    get() {
        val end = this.appointmentToDate
        return when {
            end != null -> TimeSpan.between(end, Date())
            else -> null
        }
    }

val Stop.hasAppointment
    get() = this.dateEnd != null

val Stop.appointmentState: AppointmentState
    get() {
        val timeLeft = this.appointmentTimeLeft
        return when {
            timeLeft == null -> AppointmentState.NONE
            timeLeft.totalMinutes < 0 -> AppointmentState.OVERDUE
            timeLeft.totalMinutes <= 30 -> AppointmentState.SOON
            timeLeft.totalMinutes < 60 -> AppointmentState.UPCOMING
            else -> AppointmentState.NONE
        }
    }