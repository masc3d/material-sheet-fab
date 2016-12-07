package org.deku.leoz.time

import org.deku.leoz.rest.entity.ShortTime
import java.util.*

/**
 * Short date/time conversion methods
 * Created by masc on 06/12/2016.
 */

fun java.sql.Time.toShortTime(): ShortTime {
    return ShortTime(this.toString())
}

fun Date.toShortTime(): ShortTime {
    return ShortTime(this)
}