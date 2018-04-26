package org.deku.leoz.time

import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.debug
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
        log.debug { ShortDate("2018-04-26").date }
    }
}