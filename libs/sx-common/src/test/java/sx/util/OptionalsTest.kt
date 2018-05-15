package sx.util

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import java.util.*

/**
 * Created by masc on 15.05.18.
 */
@Category(StandardTest::class)
class OptinoalsTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testNullableOptional() {
        data class Entity(
                val field1: Optional<String>?
        )

        val e1 = Entity(
                field1 = null
        )

        val e2 = Entity(
                field1 = Optional.empty()
        )

        val e3 = Entity(
                field1 = Optional.of("value")
        )

        // Missing
        Assert.assertEquals(e1.field1, null)

        // Empty / null
        Assert.assertEquals(e2.field1?.isPresent, false)

        // Value
        Assert.assertEquals(e3.field1?.get(), "value")
    }
}