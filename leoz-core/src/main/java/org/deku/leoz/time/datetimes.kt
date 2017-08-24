package org.deku.leoz.time

import org.deku.leoz.service.entity.ShortTime
import sx.time.toLocalDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Short date/time conversion methods
 * Created by masc on 06/12/2016.
 */

fun java.sql.Time.toShortTime(): ShortTime {
    return ShortTime(this.toString())
}

fun Date.toShortTime(): ShortTime {
    return ShortTime(this)
}

fun Date.toString_ddMMyyyy_PointSeparated(): String {
    return SimpleDateFormat("dd.MM.yyyy").format(this)
}

fun Date.toDateWithoutTime(): Date {
    return SimpleDateFormat("yyyy-MM-dd HH:mm").parse(this.toLocalDate().toString() + (" 00:00"))
}

fun Date.toDateOnlyTime(): Date {
    return SimpleDateFormat("yyyy-MM-dd HH:mm").parse("1899-12-30 " + (this.toShortTime().toString()))
}