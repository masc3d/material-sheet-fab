package sx.io.serialization

import org.xerial.snappy.SnappyInputStream
import org.xerial.snappy.SnappyOutputStream
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


// Extension methods for chaining serialized output

/**
 * Transform serializer output/input streams, useful for chaining in eg compression and other transformations
 */
fun Serializer.transform(outputTransformer: (OutputStream) -> OutputStream,
                         inputTransformer: (InputStream) -> InputStream)
        : Serializer {
    return object : Serializer {
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
