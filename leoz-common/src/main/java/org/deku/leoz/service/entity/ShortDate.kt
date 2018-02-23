package org.deku.leoz.service.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.text.SimpleDateFormat
import java.util.*

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortDate {
    companion object {
        private val format by lazy {
            SimpleDateFormat("yyyy-MM-dd")
        }
    }

    val date: Date

    constructor(localDate: String) {
        this.date = format.parse(localDate)
    }

    @JvmOverloads constructor(localDate: Date = Date()) {
        this.date = localDate
    }

    override fun toString(): String = format.format(this.date)
}
