package org.deku.leoz.time

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.slf4j.LoggerFactory
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import sx.io.serialization.Serializable
import sx.time.threeten.toDate
import sx.time.threeten.toInstantBp
import sx.time.threeten.toLocalDateTime
import java.util.*

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@Serializable(0xab08db185b3497)
@JsonSerialize(using = ToStringSerializer::class)
class ShortDate {
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

fun Date.toShortDate(): ShortDate = ShortDate(this)

