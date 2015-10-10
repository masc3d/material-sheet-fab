package org.deku.leoz.rest.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * LocalTime wrapper for rest operations, serializing to short time format (eg. "10:00")
 * Created by masc on 29.05.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortTime {
    val localTime: LocalTime

    constructor(localTime: String) {
        this.localTime = LocalTime.parse(localTime)
    }

    @JvmOverloads constructor(localTime: LocalTime = LocalTime.now()) {
        this.localTime = localTime
    }

    override fun toString(): String {
        return localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }
}
