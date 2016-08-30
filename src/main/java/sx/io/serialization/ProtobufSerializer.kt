package sx.io.serialization

import io.protostuff.LinkedBuffer
import io.protostuff.ProtobufIOUtil
import io.protostuff.runtime.RuntimeSchema
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by masc on 30/08/16.
 */
class ProtobufSerializer : Serializer {
    private class ProtoObject(
            var clazz: String = "",
            var data: ByteArray? = null) {
    }

    override fun serialize(output: OutputStream, obj: Any) {
        val lb = LinkedBuffer.allocate()

        val po = ProtoObject(
                clazz = obj.javaClass.name,
                data = ProtobufIOUtil.toByteArray(obj, RuntimeSchema.getSchema<Any>(obj.javaClass), lb))

        lb.clear()
        ProtobufIOUtil.writeTo(output, po, RuntimeSchema.getSchema(ProtoObject::class.java), lb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(input: InputStream): Any {
        val po = ProtoObject()
        ProtobufIOUtil.mergeFrom(input, po, RuntimeSchema.getSchema(ProtoObject::class.java))

        val c = Class.forName(po.clazz) as Class<Any>
        val obj = c.newInstance()
        ProtobufIOUtil.mergeFrom<Any>(po.data, obj, RuntimeSchema.getSchema<Any>(c))
        return obj
    }
}