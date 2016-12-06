package org.deku.leoz.rest.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant
import java.time.LocalDateTime

import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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

    constructor(date: Date) {
        val instant = Instant.ofEpochMilli(date.time)
        this.localTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime()
    }

    override fun toString(): String {
        return localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }
}
