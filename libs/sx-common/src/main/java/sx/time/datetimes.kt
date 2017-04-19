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

fun Date.toSqlDate(): java.sql.Date {
    return java.sql.Date(this.time)
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
    val calDate = GregorianCalendar(timezone)
    calDate.clear()
    calDate.time = date

    val cal = GregorianCalendar(timezone)
    cal.clear()
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
    val cal = GregorianCalendar(timezone)
    cal.clear()
    cal.time = this

    val calTime = GregorianCalendar(timezone)
    calTime.clear()
    calTime.time = time

    cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
    cal.set(Calendar.SECOND, calTime.get(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND))

    return cal.time
}

fun Date.addDays(amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date {
    val cal = Calendar.Builder().setTimeZone(timezone).build()
    cal.time = this
    cal.add(Calendar.DATE, amount)

    return cal.time
}

fun Date.substractDays(amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date {
    return this.addDays(amount * -1)
}