package sx.io.serialization

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 * Default java serializer
 */
object DefaultSerializer : Serializer {
    override fun serialize(output: OutputStream, obj: Any) {
        val oos = ObjectOutputStream(output)
        oos.writeObject(obj)
        oos.close()
    }

    override fun deserialize(input: InputStream): Any {
        val ois = ObjectInputStream(input)
        val o = ois.readObject()
        ois.close()
        return o
    }
}
