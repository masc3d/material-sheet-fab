package org.deku.leoz.service.entity

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
    val localTime: Date

    val format by lazy {
        SimpleDateFormat("HH:mm")
    }

    constructor(localTime: String) {
        this.localTime = this.format.parse(localTime)
    }

    @JvmOverloads constructor(localTime: Date = Date()) {
        this.localTime = localTime
    }

    override fun toString(): String {
        return this.format.format(this.localTime)
    }
}
