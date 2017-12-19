package sx.io

import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * Copies this stream to the given output stream, returning the number of bytes copied.
 * Provides progress callback support.
 *
 * **Note** It is the caller's responsibility to close both of these resources.
 * Created by masc on 06/02/2017.
 */
fun InputStream.copyTo(out: OutputStream,
                       bufferSize: Int = DEFAULT_BUFFER_SIZE,
                       progressCallback: ((p: Float, bytesCopied: Long) -> Unit)? = null,
                       length: Long? = null): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes > 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        if (progressCallback != null) {
            val p = if (length != null) bytesCopied.toFloat() * 100 / length else -1.0F
            progressCallback.invoke(p, bytesCopied)
        }
        bytes = read(buffer)
    }

    return bytesCopied
}

/**
 * Transform input stream into sequence of byte chunks
 * @param chunkSize Chunk size
 */
fun InputStream.toSequence(chunkSize: Int): Sequence<ByteArray> {
    return generateSequence {
        val buffer = ByteArray(chunkSize)

        this.read(buffer).let { bytes ->
            if (bytes <= 0)
                return@let null

            if (bytes == chunkSize)
                buffer
            else
                Arrays.copyOf(buffer, bytes)
        }
    }
}
