package sx.util

import org.apache.commons.codec.digest.DigestUtils
import java.nio.ByteBuffer
import java.util.*

/**
 * UUID extensions
 * Created by masc on 01.02.18.
 */

/**
 * Convert UUID to byte array
 * Created by masc on 01.02.18.
 */
fun UUID.toByteArray(): ByteArray =
        ByteBuffer.allocate(16)
                .putLong(this.mostSignificantBits)
                .putLong(this.leastSignificantBits)
                .array()

/**
 * Create 160-bit SHA1 hash.
 * Suitable for creating short uids from this UUID.
 * @param length Length of hash
 */
fun UUID.hashWithSha1(length: Int = 20) =
    DigestUtils.sha1Hex(this.toByteArray())
            .take(length)
