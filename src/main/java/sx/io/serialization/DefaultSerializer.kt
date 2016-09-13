package sx.io.serialization

import java.io.*

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

            // Lookup registered class
            val c = DefaultSerializer.lookup(resultClassDescriptor.serialVersionUID)
            // If there's a mapping to a different class, modify resulting class descriptor
            if (c != null && !c.name.equals(resultClassDescriptor.name)) {
                resultClassDescriptor = ObjectStreamClass.lookup(c)
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
