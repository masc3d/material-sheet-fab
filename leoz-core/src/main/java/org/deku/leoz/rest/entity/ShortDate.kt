package org.deku.leoz.rest.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortDate {
    val localDate: LocalDate

    constructor(localDate: String) {
        this.localDate = LocalDate.parse(localDate)
    }

    @JvmOverloads constructor(localDate: LocalDate = LocalDate.now()) {
        this.localDate = localDate
    }

    override fun toString(): String {
        return localDate.format(DateTimeFormatter.ISO_DATE)
    }
}
