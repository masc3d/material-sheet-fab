package sx.time

import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

val utcTimezone =  TimeZone.getTimeZone("UTC")

/**
 * Date/time conversion functions
 */
fun LocalDate.toTimestamp(): Timestamp {
    return this.toDate().toTimestamp()
}

fun LocalDate.toDate(): Date {
    return java.sql.Date.valueOf(this)
}

fun Date.toTimestamp(): Timestamp {
    return Timestamp(this.time)
}

fun Date.toLocalDate(): LocalDate {
    return java.sql.Date(this.time).toLocalDate()
}

/**
 * Replaces the date portion of a specific date and keeps the time
 * @param date Date portion
 * @param timezone The timezone of the date. This is important especially when storing dates with zero time component
 */
fun Date.replaceDate(date: Date, timezone: TimeZone = TimeZone.getDefault()): Date {
    val calDate = Calendar.Builder().setTimeZone(timezone).build()
    calDate.time = date

    val cal = Calendar.Builder().setTimeZone(timezone).build()
    cal.time = this

    cal.set(Calendar.YEAR, calDate.get(Calendar.YEAR))
    cal.set(Calendar.MONTH, calDate.get(Calendar.MONTH))
    cal.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH))

    return cal.time
}

/**
 * Replaces the time portion of a specific date and keeps the date
 * @param time Time portion
 * @param timezone The timezone of the time. This is important especially when storing dates with zero time component
 */
fun Date.replaceTime(time: Date, timezone: TimeZone = TimeZone.getDefault()): Date {
    val cal = Calendar.Builder().setTimeZone(timezone).build()
    cal.time = this

    val calTime = Calendar.Builder().setTimeZone(timezone).build()
    calTime.time = time

    cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
    cal.set(Calendar.SECOND, calTime.get(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND))

    return cal.time
}