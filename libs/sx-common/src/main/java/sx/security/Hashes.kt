package sx.security

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.*

/**
 * Algorithm name constants for use with {@link javax.crypto.Cipher}
 * Created by n3 on 27.04.17.
 */
enum class DigestType(val value: String) {
    SHA1("SHA1"),
    SHA256("SHA256")
}

/**
 * Get instance of digest
 */
fun DigestType.instance(): MessageDigest = MessageDigest.getInstance(this.value)

/**
 * Hash byte buffers and return the first 128 bits as UUID
 * @param digest digest algorithm to use
 */
fun List<ByteArray>.hash(digest: DigestType): ByteArray {
    return digest.instance().also { md ->
        this.forEach { md.update(it) }
    }.digest()
}

/**
 * Hash byte buffers and return the first 128 bits as UUID
 * @param digest digest algorithm to use
 */
fun List<ByteArray>.hashUUid(digest: DigestType): UUID {
    return ByteBuffer.wrap(this.hash(digest)).let {
        UUID(it.getLong(), it.getLong())
    }
}
