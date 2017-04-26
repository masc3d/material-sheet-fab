package sx.io.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger

/**
 * Jackson serializer
 * Created by masc on 21.04.17.
 */
class JacksonSerializer @JvmOverloads constructor(
        /**
         * Jackson object mapper to use
         */
        private val objectMapper: ObjectMapper = ObjectMapper()
) : Serializer() {

    companion object {
        val JSON_PROPERTY_TYPE = "@type"
        val JSON_PROPERTY_NAME = "@name"
        val JSON_PROPERTY_VERSION = "@v"
        val JSON_PROPERTY_DATA = "@data"
    }

    /**
     * Serialize
     */
    override fun serialize(output: OutputStream, obj: Any) {
        val node = this.objectMapper.createObjectNode()

        // Detemrine and register data type
        val dataType: Class<*>? = if (obj.javaClass.isArray) {
            if (obj.javaClass.componentType == Any::class.java) {
                val array = obj as Array<*>
                if (array.count() > 0)
                    array.get(0)!!.javaClass
                else
                    null
            } else
                obj.javaClass.componentType
        } else {
            if (obj is List<*>) {
                if (obj.count() > 0) {
                    obj.get(0)!!.javaClass
                } else {
                    null
                }
            } else {
                obj.javaClass
            }
        }

        if (dataType != null) {
            val sid = Serializer.types.register(dataType)
            // Write data type
            node.put(JSON_PROPERTY_TYPE, "0x${java.lang.Long.toHexString(sid.uid)}")
            node.put(JSON_PROPERTY_NAME, sid.name)
            node.put(JSON_PROPERTY_VERSION, sid.version)
        }

        node.putPOJO(JSON_PROPERTY_DATA, obj)

        this.objectMapper.writeValue(output, node)
    }

    /**
     * Deserialize
     */
    override fun deserialize(input: InputStream): Any {
        val tree = this.objectMapper.readTree(input)

        // Determine type
        val sidHex = tree.get(JSON_PROPERTY_TYPE)
                ?.textValue()

        val dataType: Class<*>? = if (sidHex != null) {
            val sid = BigInteger(sidHex.substring(2), 16).toLong()
            val dataType = Serializer.types.lookup(sid)?.javaClass

            dataType
        } else null

        val dataNode = tree.get(JSON_PROPERTY_DATA)

        val type = if (dataType != null) {
            if (dataNode.isArray) {
                java.lang.reflect.Array.newInstance(dataType, 0).javaClass
            } else
                dataType
        } else {
            Any::class.java
        }

        // Deserialize
        val o = this.objectMapper.treeToValue(dataNode, type)
        return o
    }

}