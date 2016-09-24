package sx.io.serialization

import org.junit.Test

/**
 * Created by masc on 13/09/16.
 */
class DefaultSerializerTest : SerializerTest() {
    private val _serializer = DefaultSerializer

    @Test
    fun testSerialization() {
        super.testSerialization(_serializer)
    }

    @Test
    fun testRefactoring() {
        super.testRefactoring(_serializer)
    }

    @Test
    fun testArraySerialization() {
        super.testArraySerialization(_serializer)
    }

    @Test
    fun testArrayRefactoring() {
        super.testArrayRefactoring(_serializer)
    }
}