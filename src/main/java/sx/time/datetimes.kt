package sx.time

import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/**
 * Date/time conversion functions
 */

fun LocalDate.toTimestamp(): Timestamp {
    return this.toDate().toTimestamp()
}

fun LocalDate.toDate(): Date {
    return java.sql.Date.valueOf(this);
}

fun Date.toTimestamp(): Timestamp {
    return Timestamp(this.time)
}

fun Date.toLocalDate(): LocalDate {
    return java.sql.Date(this.time).toLocalDate();
}