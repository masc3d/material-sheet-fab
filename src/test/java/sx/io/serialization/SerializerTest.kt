package sx.io.serialization

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.Stopwatch
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by masc on 06/09/16.
 */
abstract class SerializerTest(
        private val serializer: Serializer
) {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Serializable(0x716a49585525f0)
    data class TestContainer1(
            val obj: TestObject1? = null) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525f0
        }
    }

    @Serializable(0x716a49585525f0)
    data class TestContainerRefactored1(
            val obj: TestObjectRefactored1? = null) : java.io.Serializable {
        companion object {
            val serialVersionUID = 0x716a49585525f0
        }
    }

    @Serializable(0x716a49585525fd)
    data class TestObject1(
            val l: Long = 1,
            val s: String = "Hello",
            val i: Int = 2,
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
                    Arrays.equals(this.d, o.d) &&
                    Arrays.equals(this.dobj, o.dobj))
        }

        override fun hashCode(): Int {
            var result = l.hashCode()
            result = 31 * result + s.hashCode()
            result = 31 * result + i
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
                    Arrays.equals(this.d, o.d) &&
                    Arrays.equals(this.dobj, o.dobj))
        }

        override fun hashCode(): Int {
            var result = l.hashCode()
            result = 31 * result + s.hashCode()
            result = 31 * result + i
            result = 31 * result + Arrays.hashCode(d)
            result = 31 * result + Arrays.hashCode(dobj)
            return result
        }
    }

    /**
     * Test simple (de-)serialization
     */
    @Test
    fun testDeserialization() {
        Serializer.types.purge()

        val sobj = TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)))
        val scontainer = TestContainer1(sobj)

        val sdata = serializer.serializeToByteArray(scontainer)
        val dcontainer = serializer.deserializeFrom(sdata)

        Assert.assertTrue(dcontainer == scontainer)
    }

    /**
     * Test (de-)serialization with class refactoring/rename
     */
    @Test
    fun testRefactoring() {
        Serializer.types.purge()

        val sobj = TestObject1(dobj = arrayOf(TestObject1.TestObject2(200)))
        val scontainer = TestContainer1(sobj)

        val sdata = serializer.serializeToByteArray(scontainer)

        Serializer.types.purge()
        serializer.register(TestContainerRefactored1::class.java)
        serializer.register(TestObjectRefactored1::class.java)

        val dcontainer = serializer.deserializeFrom(sdata) as TestContainerRefactored1

        Assert.assertTrue(dcontainer.obj is TestObjectRefactored1)
    }

    /**
     * Test serialization with array as root object
     */
    @Test
    fun testArrayDeserialization() {
        Serializer.types.purge()

        val sobj = arrayOf(TestObject1(), TestObject1(), TestObject1())

        val sdata = serializer.serializeToByteArray(sobj)
        @Suppress("UNCHECKED_CAST")
        val dobj = serializer.deserializeFrom(sdata) as Array<TestObject1>

        Assert.assertTrue(Arrays.equals(sobj, dobj))
    }

    @Test
    fun testCollectionDeserialization() {
        Serializer.types.purge()

        val sobj = java.util.ArrayList(listOf(TestObject1(), TestObject1(), TestObject1()))

        val sdata = serializer.serializeToByteArray(sobj)
        @Suppress("UNCHECKED_CAST")
        val dobj = serializer.deserializeFrom(sdata)

        // Some serializers currently deserialize collections as arrays
        // This inconsistency is okay, as replicating collection type is an extended feature
        // that may not be supported by all serializers
        val cdobj = when (dobj) {
            is ArrayList<*> -> dobj.toTypedArray()
            else -> dobj as Array<*>
        }

        Assert.assertTrue(Arrays.equals(sobj.toTypedArray(), cdobj))
    }


    /**
     * Test serialization with array as root object and refactoring the root type
     */
    @Test
    fun testArrayRefactoring() {
        Serializer.types.purge()

        val sobj = arrayOf(
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200))),
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200))),
                TestObject1(dobj = arrayOf(TestObject1.TestObject2(200))))

        val sdata = serializer.serializeToByteArray(sobj)

        Serializer.types.purge()
        serializer.register(TestObjectRefactored1::class.java)

        @Suppress("UNCHECKED_CAST")
        (serializer.deserializeFrom(sdata) as Array<TestObjectRefactored1>).also {
            println(it)
        }
    }

    /**
     * Test serialization with plain object array
     */
    @Test
    fun testObjectArrayDeserialization() {
        Serializer.types.purge()

        val slist = ArrayList<Any?>(listOf(TestObject1(), TestObject1(), TestObject1()))
        val sobj = slist.toTypedArray()

        val sdata = serializer.serializeToByteArray(sobj)
        val dobj = serializer.deserializeFrom(sdata) as Array<*>

        Assert.assertTrue(Arrays.equals(sobj, dobj))
    }

    @Test
    fun testSerialization() {
        Serializer.types.purge()

        val obj = TestObject1()
        val sdata = serializer.serializeToByteArray(obj)

        InputStreamReader(ByteArrayInputStream(sdata)).readText().also {
            println(it)
        }
    }

    @Ignore
    @Test
    fun testSerializationPerformance() {
        Serializer.types.purge()

        for (i in 0..1000) {
            val sw = Stopwatch.createStarted()

            val obj = TestObject1()
            val sdata = serializer.serializeToByteArray(obj)

            InputStreamReader(ByteArrayInputStream(sdata)).readText().also {
                println("${sw} ${it}")
            }
        }
    }
}