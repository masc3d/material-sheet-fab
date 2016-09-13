package sx.io.serialization

import org.junit.Assert
import org.junit.Test

/**
 * Created by masc on 13/09/16.
 */
class DefaultSerializerTest : SerializerTest() {

    @Test
    fun testSerialization() {
        super.testSerialization(DefaultSerializer)
    }

    @Test
    fun testRefactoring() {
        super.testSerialization(DefaultSerializer)
    }
}