package org.deku.leoz.time

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.slf4j.LoggerFactory
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import sx.time.threeten.toDate
import sx.time.toLocalDateTime
import java.util.*

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@JsonSerialize(using = ToStringSerializer::class)
class ShortDate {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val formatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    }

    val date: Date

    constructor(localDate: String) {
        this.date = LocalDate.parse(localDate, formatter).toDate()
    }

    @JvmOverloads constructor(localDate: Date = Date()) {
        this.date = localDate
    }

    override fun toString(): String = formatter.format(this.date.toLocalDateTime())
}

fun Date.toShortDate(): ShortDate {
    return ShortDate(this)
}

