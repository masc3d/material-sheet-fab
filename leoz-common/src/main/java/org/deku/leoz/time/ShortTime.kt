package org.deku.leoz.time

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.text.SimpleDateFormat
import java.util.*

/**
 * LocalTime wrapper for rest operations, serializing to short time format (eg. "10:00")
 * Created by masc on 29.05.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortTime {
    companion object {
        val format by lazy {
            SimpleDateFormat("HH:mm")
        }
    }

    val localTime: Date

    constructor(localTime: String) {
        this.localTime = format.parse(localTime)
    }

    @JvmOverloads constructor(localTime: Date = Date()) {
        this.localTime = localTime
    }

    override fun toString(): String {
        return format.format(this.localTime)
    }
}

fun Date.toShortTime(): ShortTime {
    return ShortTime(this)
}
