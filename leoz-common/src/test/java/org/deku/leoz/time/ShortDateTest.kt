package org.deku.leoz.time

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.debug
import sx.time.threeten.toDate
import sx.time.threeten.toLocalDateTime
import sx.time.toDate
import java.util.*

/**
 * Created by masc on 26.04.18.
 */
@Category(StandardTest::class)
class ShortDateTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testFormat() {
        log.debug { Date().toShortDate() }
    }

    @Test
    fun testParse() {
        val dateString ="2018-04-29"

        val shortDate = ShortDate(dateString)

        log.debug { "${dateString} -> ${shortDate}" }

        Assert.assertEquals(dateString, shortDate.toString())

        log.debug { shortDate.date.toLocalDateTime() }
        log.debug { Date().toLocalDateTime().toDate() }
    }
}