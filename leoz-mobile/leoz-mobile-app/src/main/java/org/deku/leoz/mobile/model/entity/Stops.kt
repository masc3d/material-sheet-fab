package org.deku.leoz.mobile.model.entity

import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.model.median
import sx.time.TimeSpan
import sx.time.plusMinutes
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

/**
 * Appointment start for this stop, aggregated from all referring tasks
 */
val Stop.appointmentStart: Date?
    get() = this.tasks.map { it.appointmentStart }.filterNotNull().max()

/**
 * Appointment end for this stop, aggregated from all referring tasks
 */
val Stop.appointmentEnd: Date?
    get() = this.tasks.map { it.appointmentEnd }.filterNotNull().min()

/**
 * Appointment start date
 */
val Stop.appointmentStartDate: Date?
    get() {
        return this.appointmentStart?.let {
            Date().replaceTime(it)
        }
    }

/**
 * Appointment end date
 */
val Stop.appointmentEndDate: Date?
    get() {
        return this.appointmentEnd?.let {
            Date().replaceTime(it)
        }
    }

/**
 * Time left until the end of appointment time frame is reached
 */
val Stop.appointmentTimeLeft: TimeSpan?
    get() {
        val end = this.appointmentEndDate
        return when {
            end != null -> TimeSpan.between(Date(), end)
            else -> null
        }
    }

/** Indicates if stop has appointment(s) */
val Stop.hasAppointment
    get() = this.appointmentEnd != null

/** Appointment state */
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