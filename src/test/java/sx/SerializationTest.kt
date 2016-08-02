package sx

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import io.protostuff.LinkedBuffer
import io.protostuff.ProtobufIOUtil
import io.protostuff.ProtostuffIOUtil
import io.protostuff.runtime.RuntimeSchema
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info
import java.io.*

/**
 * Created by masc on 12.10.15.
 */
class SerializationTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    class ProtostuffObject(
            var clazz: String = "",
            var data: ByteArray? = null) {
    }

    data class TestClass1(
            var field1: String = "Hello")
    :
            Serializable {
        companion object {
            val serialVersionUID = 1584643871336872667L
        }
    }

    data class TestClass2(
            var field2: String = "Hello")
    :
            Serializable {
        companion object {
            val serialVersionUID = 1584643871336872668L
        }
    }

    val kryo = Kryo()

    init {
        kryo.setDefaultSerializer(CompatibleFieldSerializer::class.java)
    }

    /**
     * Mainly for testing readability of serialUID of a kotlin class
     */
    @Test
    fun testUid() {
        Assert.assertEquals(ObjectStreamClass.lookup(TestClass1::class.java).serialVersionUID, TestClass1.serialVersionUID)
    }

    private fun serializeKryo(outStream: OutputStream, o: Any) {
        val out = Output(outStream)
        this.kryo.writeClassAndObject(out, o)
        out.close()
    }

    private fun deserializeKryo(inStream: InputStream): Any {
        val i = Input(inStream)
        val o = this.kryo.readClassAndObject(i)
        i.close()
        return o
    }

    private fun serializeProtostuff(stream: OutputStream, o: Any) {
        val lb = LinkedBuffer.allocate()

        val po = ProtostuffObject(
                clazz = o.javaClass.name,
                data = ProtobufIOUtil.toByteArray(o, RuntimeSchema.getSchema<Any>(o.javaClass), lb))

        lb.clear()
        ProtobufIOUtil.writeTo(stream, po, RuntimeSchema.getSchema(ProtostuffObject::class.java), lb)
    }

    private fun deserializeProtostuff(stream: InputStream): Any {
        val o = ProtostuffObject()
        ProtobufIOUtil.mergeFrom(stream, o, RuntimeSchema.getSchema(ProtostuffObject::class.java))

        val c = Class.forName(o.clazz) as Class<Any>
        val o2 = c.newInstance()
        ProtobufIOUtil.mergeFrom<Any>(o.data, o2, RuntimeSchema.getSchema<Any>(c))
        return o2
    }

    private fun serializeJava(outStream: OutputStream, o: Any) {
        val oos = ObjectOutputStream(outStream)
        oos.writeObject(o)
        oos.close()
    }

    private fun deserializeJava(inStream: InputStream): Any {
        val ois = ObjectInputStream(inStream)
        val o = ois.readObject()
        ois.close()
        return o
    }

    @Test
    fun testSerialize() {
        val sw = Stopwatch.createStarted()

        for (i: Int in 0..1000000) {
            var objIn = TestClass1()

            var out = ByteArrayOutputStream()
//        this.serializeKryo(out, TestClass1("meh"))
            this.serializeProtostuff(out, TestClass1("meh"))

            var content = out.toByteArray()

            var input = ByteArrayInputStream(content)
//        var objOut = this.deserializeKryo(input)
            var objOut = this.deserializeProtostuff(input)
        }

        log.info("Completed in [$sw]")

    }
}
