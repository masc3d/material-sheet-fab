package org.deku.leoz.model

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest

/**
 * Created by masc on 05.07.17.
 */
@Category(StandardTest::class)
class UnitNumberTests {
    val UNIT_NO_LABEL = "010000000009"
    val GLS_UNIT_NO_LABEL = "338500000008"

    @Test
    fun testGlsUnitNumber() {
        val gun = GlsUnitNumber.parseLabel(GLS_UNIT_NO_LABEL)

        // Test conversion
        val un = gun.toUnitNumber()
        Assert.assertEquals("83350000000", un.value)
    }

    @Test
    fun testUnitNumber() {
        val un = UnitNumber.parseLabel(UNIT_NO_LABEL)
    }
}