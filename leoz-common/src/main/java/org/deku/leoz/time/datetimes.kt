package org.deku.leoz.time

import org.threeten.bp.format.DateTimeFormatter
import sx.time.threeten.toLocalDateTime
import sx.time.toCalendar
import java.util.*

object DateFormats {
    val gregorianLongDate by lazy { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val gregorianLongDateTime by lazy { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS") }
}

fun Date.toGregorianLongDateString(): String {
    return DateFormats.gregorianLongDate.format(this.toLocalDateTime())
}

fun Date.toGregorianLongDateTimeString(): String {
    return DateFormats.gregorianLongDateTime.format(this.toLocalDateTime())
}

fun Date.toDateWithoutTime(): Date =
        this.toCalendar().let {
            it.set(Calendar.HOUR_OF_DAY, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
            it.set(Calendar.MILLISECOND, 0)
            it.time
        }

fun Date.toTimeWithoutDate(): Date =
        this.toCalendar().let {
            it.set(Calendar.YEAR, 1899)
            it.set(Calendar.MONTH, Calendar.DECEMBER)
            it.set(Calendar.DAY_OF_MONTH, 30)
            it.time
        }
