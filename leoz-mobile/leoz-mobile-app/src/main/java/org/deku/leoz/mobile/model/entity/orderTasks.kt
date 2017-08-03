package org.deku.leoz.mobile.model.entity

import java.util.*

/**
 * Determines if this order task has compatible appointment criteria with another
 * Created by masc on 03.08.17.
 */
fun OrderTask.hasCompatibleAppointmentsWith(other: OrderTask): Boolean {
    val tDateStart = this.dateStart
    val tDateEnd = this.dateEnd
    val oDateStart = this.dateEnd
    val oDateEnd = this.dateEnd

    // If any has no appointment they are compatible
    if ((tDateStart == null && tDateEnd == null) ||
            (oDateStart == null && oDateEnd == null))
        return true

    // If any has fixed appointpents, times have to match precisely
    if (this.notBeforeStart || other.notBeforeStart) {
        return (tDateStart == oDateStart && tDateEnd == oDateEnd)
    }

    // Otherwise check if times are compatible
    val startDates = arrayOf(
            tDateStart ?: Date(Long.MIN_VALUE),
            oDateStart ?: Date(Long.MIN_VALUE)
    )

    val endDates = arrayOf(
            tDateEnd ?: Date(Long.MAX_VALUE),
            oDateEnd ?: Date(Long.MAX_VALUE)
    )

    val maxStartDate = startDates.max() ?: throw IllegalStateException()
    val minEndDate = endDates.min() ?: throw IllegalStateException()

    return maxStartDate < minEndDate
}