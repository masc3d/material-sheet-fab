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
            val cls: Class<*> = Any::class.java,
            val d: Array<Int> = listOf(1, 2, 3).toTypedArray(),
            val al: List<String> = mutableListOf("Hello1", "Hello2"),
            val dobj: Array<TestObject2> = listOf(TestObject1.TestObject2()).toTypedArray()
    ) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525fd
        }

        @Serializable(0x9c826bbb11c253)
        data class TestObject2(
                val t: Int = 1
        ) : java.io.Serializable {
            companion object {
                val serialVersionUID = 0x9c826bbb11c253
            }
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? TestObject1
            if (o == null)
                return false

            return (this.l == o.l &&
                    this.s == o.s &&
                    this.i == o.i &&
                    this.cls == o.cls &&
                    Arrays.equals(this.d, o.d) &&
                    Arrays.equals(this.dobj, o.dobj))
        }

        override fun hashCode(): Int{
            var result = l.hashCode()
            result = 31 * result + s.hashCode()
            result = 31 * result + i
            result = 31 * result + cls.hashCode()
            result = 31 * result + Arrays.hashCode(d)
            result = 31 * result + Arrays.hashCode(dobj)
            return result
        }
    }

    @Serializable(0x716a49585525fd)
    data class TestObjectRefactored1(
            val l: Long = 1,
            val s: String = "Hello",
            val i: Int = 2,
            val cls: Class<*> = Any::class.java,
            val d: Array<Int> = listOf(1, 2, 3).toTypedArray(),
            val al: List<String> = mutableListOf("Hello1", "Hello2"),
            val dobj: Array<TestObjectRefactored1.TestObjectRefactored2> = listOf(TestObjectRefactored1.TestObjectRefactored2()).toTypedArray()
    ) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525fd
        }

        @Serializable(0x9c826bbb11c253)
        data class TestObjectRefactored2(
                val t: Int = 1
        ) : java.io.Serializable {
            companion object {
                val serialVersionUID = 0x9c826bbb11c253
            }
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? TestObject1
            if (o == null)
                return false

            return (this.l == o.l &&
                    this.s == o.s &&
                    this.i == o.i &&
                    this.cls == o.cls &&
                    Arrays.equals(this.d, o.d) &&
                    Arrays.equals(this.dobj, o.dobj))
        }

        override fun hashCode(): Int{
            var result = l.hashCode()
            result = 31 * result + s.hashCode()
            result = 31 * result + i
            result = 31 * result + cls.hashCode()
            result = 31 * result + Arrays.hashCode(d)
            result = 31 * result + Arrays.hashCode(dobj)
            return result
        }
    }

    /**
     * Test simple (de-)serialization
     */
    fun testSerialization(serializer: Serializer) {
        Serializer.types.purge()

        val sobj = TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)))
        val scontainer = TestContainer(sobj)

        val sdata = serializer.serializeToByteArray(scontainer)
        val dcontainer = serializer.deserializeFrom(sdata)

        Assert.assertTrue(dcontainer.equals(scontainer))
    }

    /**
     * Test (de-)serialization with class refactoring/rename
     */
    fun testRefactoring(serializer: Serializer) {
        Serializer.types.purge()

        val sobj = TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)))
        val scontainer = TestContainer(sobj)

        val sdata = serializer.serializeToByteArray(scontainer)

        Serializer.types.purge()
        serializer.register(TestObjectRefactored1::class.java)

        val dcontainer = serializer.deserializeFrom(sdata) as TestContainer<*>

        Assert.assertTrue(dcontainer.obj is TestObjectRefactored1)
    }

    /**
     * Test serialization with array as root object
     */
    fun testArraySerialization(serializer: Serializer) {
        Serializer.types.purge()

        val sobj = arrayOf(TestObject1(), TestObject1(), TestObject1())

        val sdata = serializer.serializeToByteArray(sobj)
        @Suppress("UNCHECKED_CAST")
        val dobj = serializer.deserializeFrom(sdata) as Array<TestObject1>

        Assert.assertTrue(Arrays.equals(sobj, dobj))
    }

    /**
     * Test serialization with array as root object and refactoring the root type
     */
    fun testArrayRefactoring(serializer: Serializer) {
        Serializer.types.purge()

        val sobj = arrayOf(
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)), cls = TestObject1::class.java),
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)), cls = TestObject1::class.java),
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)), cls = TestObject1::class.java))

        val sdata = serializer.serializeToByteArray(sobj)

        Serializer.types.purge()
        serializer.register(TestObjectRefactored1::class.java)

        @Suppress("UNCHECKED_CAST")
        val dobj = serializer.deserializeFrom(sdata) as Array<TestObjectRefactored1>

        Assert.assertTrue(dobj is Array<TestObjectRefactored1>)
        Assert.assertTrue(dobj.get(0).cls.equals(TestObjectRefactored1::class.java))
    }

    /**
     * Test serialization with plain object array
     */
    fun testObjectArraySerialization(serializer: Serializer) {
        Serializer.types.purge()

        val slist = ArrayList<Any?>(listOf(TestObject1(), TestObject1(), TestObject1()))
        val sobj = slist.toTypedArray()

        val sdata = serializer.serializeToByteArray(sobj)
        val dobj = serializer.deserializeFrom(sdata) as Array<*>

        Assert.assertTrue(Arrays.equals(sobj, dobj))
    }
}