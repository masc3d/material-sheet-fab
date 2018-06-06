package org.deku.leoz.time

import org.deku.leoz.time.toDateWithoutTime
import org.deku.leoz.time.toTimeWithoutDate
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*
import sx.log.slf4j.*
/**
 * Created by masc on 20.11.17.
 */
class DateTests {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testToDateWithoutTime() {
        log.trace {
            Date().toDateWithoutTime()
        }
    }

    @Test
    fun testToDateOnlyTime() {
        log.trace {
            Date().toTimeWithoutDate()
        }
    }
}