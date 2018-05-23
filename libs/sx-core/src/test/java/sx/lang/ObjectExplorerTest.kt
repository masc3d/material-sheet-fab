package sx.lang

import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest
import sx.lang.objectexplorer.ObjectGraphMeasurer

/**
 * Created by masc on 23.05.18.
 */
@Category(StandardTest::class)
class ObjectExplorerTest {
    @Test
    fun testHashMapSize() {
        println(ObjectGraphMeasurer.measure(
                mutableMapOf("a" to 1, "b" to 2)
        ))
    }
}