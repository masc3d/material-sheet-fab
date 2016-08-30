package sx.io.serialization

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Generic serialization interface
 * Created by masc on 30/08/16.
 */
interface Serializer {
    fun serialize(output: OutputStream, obj: Any)
    fun deserialize(input: InputStream): Any

    fun serializeToByteArray(obj: Any): ByteArray {
        val bos = ByteArrayOutputStream()
        this.serialize(bos, obj)
        return bos.toByteArray()
    }

    fun deserializeFrom(byteArray: ByteArray): Any {
        return this.deserialize(ByteArrayInputStream(byteArray))
    }
}
