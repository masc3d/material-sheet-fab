package sx

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.junit.Assert
import org.junit.Test
import java.io.*

/**
 * Created by masc on 12.10.15.
 */
class SerializationTest {

    data class TestClass1(
            var field1: String = "Hello")
    :
            Serializable {
        companion object {
            val serialVersionUID = 1584643871336872667L
        }
    }

    data class TestClass2(
            var field1: String = "Hello")
    :
            Serializable {
        companion object {
            val serialVersionUID = 1584643871336872668L
        }
    }

    val kryo = Kryo()

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
        var objIn = TestClass1()

        var out = ByteArrayOutputStream()
        this.serializeKryo(out, TestClass1())

        var content = out.toByteArray()

        var input = ByteArrayInputStream(content)
        var objOut = this.deserializeKryo(input)

        println(objOut)
    }
}
