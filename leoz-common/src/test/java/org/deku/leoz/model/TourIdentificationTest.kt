package org.deku.leoz.model

import org.junit.Assert
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

    private val LABELS = listOf(
        "DK;TR;310;6BA54459-0504-49D9-A536-1194BA6E4EB4"
    )

    @Test
    fun testParseLabel() {
        LABELS.forEach { label ->
            log.trace {
                TourIdentification.parseLabel(label).also {
                    Assert.assertEquals(it.value.label, label)
                }
            }
        }
    }
}