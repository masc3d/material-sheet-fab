package org.deku.leoz.rest.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortDate {
    private val format by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    val date: Date

    constructor(localDate: String) {
        this.date = this.format.parse(localDate)
    }

    @JvmOverloads constructor(localDate: Date = Date()) {
        this.date = localDate
    }

    override fun toString(): String {
        return this.format.format(this.date)
    }
}
