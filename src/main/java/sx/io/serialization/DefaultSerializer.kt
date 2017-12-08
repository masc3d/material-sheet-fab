package sx.io.serialization

import java.io.*
import java.lang.reflect.Array

/**
 * Default java serializer
 */
object DefaultSerializer : Serializer() {
    /**
     * Customized `ObjectInputStream` for identifying class by @Serializable annotation
     */
    private class ObjectInputStream(`in`: InputStream)
    : java.io.ObjectInputStream(`in`) {
        @Throws(IOException::class, ClassNotFoundException::class)
        override protected fun readClassDescriptor(): ObjectStreamClass {
            var resultClassDescriptor = super.readClassDescriptor()

            val resultClass = Class.forName(resultClassDescriptor.name)

            val lookupUid: Long
            if (resultClass.isArray) {
                val osc = ObjectStreamClass.lookup(resultClass.componentType)
                lookupUid = osc.serialVersionUID
            } else {
                lookupUid = resultClassDescriptor.serialVersionUID
            }

            // Lookup registered class
            val finalClass = if (resultClass.isArray) {
                val lc = DefaultSerializer.lookup(lookupUid)
                if (lc != null) Array.newInstance(lc.javaClass, 0).javaClass else null
            } else {
                DefaultSerializer.lookup(lookupUid)?.javaClass
            }

            // If there's a mapping to a different class, modify resulting class descriptor
            if (finalClass != null && finalClass != resultClass) {
                resultClassDescriptor = ObjectStreamClass.lookup(finalClass)
            }

            return resultClassDescriptor
        }
    }

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
