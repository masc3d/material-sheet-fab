package sx.io

import java.io.InputStream
import java.io.OutputStream

/**
 * Copies this stream to the given output stream, returning the number of bytes copied.
 * Provides progress callback support.
 *
 * **Note** It is the caller's responsibility to close both of these resources.
 * Created by masc on 06/02/2017.
 */
public fun InputStream.copyTo(out: OutputStream,
                              bufferSize: Int = DEFAULT_BUFFER_SIZE,
                              progressCallback: ((p: Float, bytesCopied: Long) -> Unit)? = null,
                              length: Long? = null): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
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
