package sx.io.serialization

import org.junit.Assert
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by masc on 06/09/16.
 */
open class SerializerTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Serializable(0x716a49585525f0)
    data class TestContainer<T>(
            val obj: T? = null) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525f0
        }
    }

    @Serializable(0x716a49585525fd)
    data class TestObject1(
            val l: Long = 1,
            val s: String = "Hello",
            val i: Int = 2,
            val d: Array<Int> = listOf(1, 2, 3).toTypedArray()
    ) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525fd
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? TestObject1
            if (o == null)
                return false

            return (this.l == o.l &&
                    this.s == o.s &&
                    this.i == o.i &&
                    Arrays.equals(this.d, o.d))
        }
    }

    @Serializable(0x716a49585525fd)
    data class TestObjectRefactored1(
            val l: Long = 1,
            val s: String = "Hello",
            val i: Int = 2,
            val d: Array<Int> = listOf(1, 2, 3).toTypedArray()
    ) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525fd
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? TestObject1
            if (o == null)
                return false

            return (this.l == o.l &&
                    this.s == o.s &&
                    this.i == o.i &&
                    Arrays.equals(this.d, o.d))
        }
    }

    @Serializable(0x9c826bbb11c253)
    data class TestObject2(
            val t: TestObject1 = TestObject1()
    ) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x9c826bbb11c253
        }
    }

    fun testSerialization(serializer: Serializer) {
        Serializer.purge()

        val container = TestContainer(TestObject1())

        serializer.register(TestContainer::class.java)
        serializer.register(TestObject1::class.java)

        val data = serializer.serializeToByteArray(container)
        val dcontainer = serializer.deserializeFrom(data)

        Assert.assertTrue(dcontainer.equals(container))
    }

    fun testRefactoring(serializer: Serializer) {
        Serializer.purge()

        val container = TestContainer(TestObject1())

        serializer.register(TestContainer::class.java)
        serializer.register(TestObject1::class.java)

        val data = serializer.serializeToByteArray(container)

        Serializer.purge()
        serializer.register(TestObjectRefactored1::class.java)

        val dcontainer = serializer.deserializeFrom(data) as TestContainer<*>

        Assert.assertTrue(dcontainer.obj is TestObjectRefactored1)
    }
}