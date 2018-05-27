package sx.time.threeten

import org.threeten.bp.*
import java.util.*

/**
 * Threeten bp kotlin extensions
 * Created by masc on 10.10.17.
 */

fun Date.toInstantBp(): Instant = DateTimeUtils.toInstant(this)

fun Instant.toDate(): Date = DateTimeUtils.toDate(this)

fun Instant.toSqlTimestamp(): java.sql.Timestamp = DateTimeUtils.toSqlTimestamp(this)

fun Calendar.toInstantBp(): Instant = DateTimeUtils.toInstant(this)

fun Calendar.toZonedDateTime(): ZonedDateTime = DateTimeUtils.toZonedDateTime(this)

fun ZonedDateTime.toCalendar(): GregorianCalendar = DateTimeUtils.toGregorianCalendar(this)

fun TimeZone.toZoneIdBp(): ZoneId = DateTimeUtils.toZoneId(this)

fun ZoneId.toTimeZone(): TimeZone = DateTimeUtils.toTimeZone(this)

fun LocalDate.toSqlDate(): java.sql.Date = DateTimeUtils.toSqlDate(this)

fun LocalDate.toDate(): java.util.Date = DateTimeUtils.toSqlDate(this)

fun LocalTime.toSqlTime(): java.sql.Time = DateTimeUtils.toSqlTime(this)

fun LocalTime.toDate(): java.util.Date = DateTimeUtils.toSqlTime(this)

fun LocalDateTime.toSqlTimetamp(): java.sql.Timestamp = DateTimeUtils.toSqlTimestamp(this)

fun LocalDateTime.toDate(): Date = Date(this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

fun java.sql.Date.toLocalDateBp(): LocalDate = DateTimeUtils.toLocalDate(this)

fun java.sql.Time.toLocalTimeBp(): LocalTime = DateTimeUtils.toLocalTime(this)

fun java.sql.Timestamp.toLocalDateTimeBp(): LocalDateTime = DateTimeUtils.toLocalDateTime(this)

fun java.sql.Timestamp.toInstantBp(): Instant = DateTimeUtils.toInstant(this)

fun java.util.Date.toLocalDateTime(): LocalDateTime = this.toInstantBp().atZone(ZoneId.systemDefault()).toLocalDateTime()

