package org.deku.leoz.time

import org.deku.leoz.service.entity.ShortTime
import sx.time.toCalendar
import sx.time.toLocalDate
import java.text.SimpleDateFormat
import java.util.*

object DateFormats {
    val gregorianLongDate by lazy { SimpleDateFormat("dd.MM.yyyy") }
    val gregorianLongDateTime by lazy { SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS") }
}

fun Date.toShortTime(): ShortTime {
    return ShortTime(this)
}

fun Date.toGregorianLongDateString(): String {
    return DateFormats.gregorianLongDate.format(this)
}

fun Date.toGregorianLongDateTimeString(): String {
    return DateFormats.gregorianLongDateTime.format(this)
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
            it.set(Calendar.MONTH, 12)
            it.set(Calendar.DAY_OF_MONTH, 30)
            it.time
        }
