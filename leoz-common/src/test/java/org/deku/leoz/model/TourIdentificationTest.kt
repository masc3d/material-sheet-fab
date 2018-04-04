package org.deku.leoz.model

import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.trace

/**
 * Created by masc on 04.04.18.
 */
@Category(StandardTest::class)
class TourIdentificationTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testParseLabel() {
        log.trace {
            TourIdentification.parseLabel("<deku-tour id=\"701\" uid=\"6ba54459-0504-49d9-a536-1194ba6e4eb4\"/>")
        }
    }
}