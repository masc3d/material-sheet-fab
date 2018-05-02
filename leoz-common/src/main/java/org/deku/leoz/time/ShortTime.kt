package org.deku.leoz.time

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import sx.time.threeten.toDate
import sx.time.threeten.toLocalDateTime
import java.util.*

/**
 * LocalTime wrapper for rest operations, serializing to short time format (eg. "10:00")
 * Created by masc on 29.05.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortTime {
    companion object {
        val format by lazy { DateTimeFormatter.ofPattern("HH:mm") }
    }

    val localTime: Date

    constructor(localTime: String) {
        this.localTime = LocalTime.parse(localTime, format).toDate()
    }

    @JvmOverloads constructor(localTime: Date = Date()) {
        this.localTime = localTime
    }

    override fun toString(): String = format.format(this.localTime.toLocalDateTime())
}

fun Date.toShortTime(): ShortTime = ShortTime(this)
