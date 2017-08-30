package sx.time

import java.sql.Timestamp
import java.time.LocalDate
import java.util.*

val utcTimezone = TimeZone.getTimeZone("UTC")

/**
 * Date/time conversion functions
 */
fun LocalDate.toTimestamp(): Timestamp = this.toDate().toTimestamp()

fun LocalDate.toDate(): Date = java.sql.Date.valueOf(this)

fun Date.toTimestamp(): Timestamp = Timestamp(this.time)

fun Date.toSqlDate(): java.sql.Date = java.sql.Date(this.time)

fun Date.toLocalDate(): LocalDate = java.sql.Date(this.time).toLocalDate()

/**
 * Replaces the date portion of a specific date and keeps the time
 * @param date Date portion
 * @param timezone The timezone of the date. This is important especially when storing dates with zero time component
 */
fun Date.replaceDate(date: Date, timezone: TimeZone = TimeZone.getDefault()): Date {
    val calDate = date.toCalendar(timezone)

    val cal = this.toCalendar(timezone)

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
    val cal = this.toCalendar(timezone)
    val calTime = time.toCalendar(timezone)

    cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
    cal.set(Calendar.SECOND, calTime.get(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND))

    return cal.time
}

/**
 * Convenience method for perfoming `add` modifications on calendar fields
 */
private fun Date.plus(field: Int, amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date {
    val cal = this.toCalendar(timezone)
    cal.add(field, amount)
    return cal.time
}

/**
 * Add days
 */
fun Date.plusDays(amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date =
        this.plus(Calendar.DATE, amount, timezone = timezone)

/**
 * Add hours
 */
fun Date.plusHours(amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date =
        this.plus(Calendar.HOUR, amount, timezone = timezone)

/**
 * Add minutes
 */
fun Date.plusMinutes(amount: Int, timezone: TimeZone = TimeZone.getDefault()): Date =
        this.plus(Calendar.MINUTE, amount, timezone = timezone)


/**
 * Convenience method for converting date to calendar
 */
fun Date.toCalendar(timezone: TimeZone = TimeZone.getDefault()): Calendar {
    val cal = GregorianCalendar(timezone)
    cal.clear()
    cal.time = this
    return cal
}

/**
 * Minus minutes
 */
fun Date.minusMinutes(amount:Int,timezone: TimeZone= TimeZone.getDefault()): Date =
        this.plus(Calendar.MINUTE,Math.abs(amount)*-1, timezone = timezone)