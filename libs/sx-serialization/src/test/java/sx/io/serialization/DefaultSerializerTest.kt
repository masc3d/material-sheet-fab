package sx.io.serialization

import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest

/**
 * Created by masc on 13/09/16.
 */
@Category(StandardTest::class)
class DefaultSerializerTest : SerializerTest(DefaultSerializer) {
    /**
     * Unsupported for `DefaultSerializer`
     */
    override fun testObjectArrayDeserialization() {
    }
}