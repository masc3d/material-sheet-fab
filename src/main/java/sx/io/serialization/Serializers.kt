package sx.io.serialization

import org.xerial.snappy.SnappyInputStream
import org.xerial.snappy.SnappyOutputStream
import java.io.InputStream
import java.io.ObjectStreamClass
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Serializable annotation
 * @param uid Unique UUID (can be a 128-bit GUID or 64-bit short UID (eg. http://www.shortguid.com/#/guid/uid-64)
 */
@Target(AnnotationTarget.CLASS)
annotation class Serializable @JvmOverloads constructor(val uid: Long = 0, val name: String = "", val version: Int = 1)

/**
 * Serializable type metadata container
 */
class SerializableType private constructor (
        val javaClass: Class<*>,
        val uid: Long = 0,
        val name: String = "",
        val version: Int = 0
) {
    companion object {
        /**
         * Build serializable type from class/annotation
         * @param cls Class
         * @return SerialiazableType or null if class is not serializable
         * @throws IllegalStateException When there's both @Serializable and Serializable implementation with mismatched UIDs
         */
        fun from(javaClass: Class<*>): SerializableType? {
            val annotation = javaClass.getAnnotation(Serializable::class.java)
            val objectStreamClass = ObjectStreamClass.lookup(javaClass)

            val name: String = annotation?.name.let {
                when {
                    it == null || it.isNullOrEmpty() -> javaClass.simpleName
                    else -> it
                }
            }
            val version = annotation?.version ?: 1

            val uid = if (annotation != null) {
                // Use annotation id. Make sure it matches java's serialVersionUID if applicable
                if (objectStreamClass != null && objectStreamClass.serialVersionUID != 0L && annotation.uid != objectStreamClass.serialVersionUID) {
                    throw IllegalStateException("Class ${javaClass} has mismatch @Serializable uid [${java.lang.Long.toHexString(annotation.uid)}] with serialVersionUID [${java.lang.Long.toHexString(objectStreamClass.serialVersionUID)}]")
                }

                if (annotation.uid != 0L)
                    annotation.uid
                else {
                    var hash = name.hashCode().toLong()
                    hash = 31 * hash + version.hashCode()
                    hash
                }
            } else {
                if (objectStreamClass != null && objectStreamClass.serialVersionUID != 0L)
                    objectStreamClass.serialVersionUID
                else
                    null
            }

            return if (uid != null) {
                SerializableType(
                        javaClass = javaClass,
                        uid = uid,
                        name = name,
                        version = version
                )
            } else null
        }
    }
}

// Extension methods for chaining serialized output

/**
 * Transform serializer output/input streams, useful for chaining in eg compression and other transformations
 */
fun Serializer.transform(outputTransformer: (OutputStream) -> OutputStream,
                         inputTransformer: (InputStream) -> InputStream)
        : Serializer {
    return object : Serializer() {
        override fun serialize(output: OutputStream, obj: Any) {
            this@transform.serialize(outputTransformer(output), obj)
        }

        override fun deserialize(input: InputStream): Any {
            return this@transform.deserialize(inputTransformer(input))
        }
    }
}

/**
 * Serializer with gzip compression
 */
val Serializer.gzip: Serializer
    get() = this.transform({ GZIPOutputStream(it) }, { GZIPInputStream(it) })


/**
 * Serializer with snappy compression
 */
val Serializer.snappy: Serializer
    get() = this.transform({ SnappyOutputStream(it) }, { SnappyInputStream(it) })
