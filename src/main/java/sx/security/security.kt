package sx.security

import org.junit.Assert
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Algorithm name constants for use with {@link javax.crypto.Cipher}
 * Created by n3 on 27.04.17.
 */
enum class DigestType(val value: String) {
    SHA1("SHA1"),
    SHA256("SHA256")
}

/**
 * Algorithm name constants for use with {@link java.security.MessageDigest}
 * Created by n3 on 27.04.17.
 */
enum class CipherType(val value: String) {
    AES("AES")
}

/**
 * Get instance of digest
 */
fun DigestType.getInstance(): MessageDigest {
    return MessageDigest.getInstance(this.value)
}

/**
 * Get instance of cipher type
 */
fun CipherType.getInstance(): Cipher {
    return Cipher.getInstance(this.value)
}

/**
 * Create cipher-specific secret key
 */
fun CipherType.createKey(key: ByteArray): SecretKey {
    return SecretKeySpec(key, this.value)
}

/**
 * Simple one-shot encryption
 * @param cipherType Cipher type to use
 * @param key 128bit key
 */
fun ByteArray.encrypt(cipherType: CipherType, key: ByteArray): ByteArray {
    val secretKey = cipherType.createKey(key)

    val c = cipherType.getInstance()
    c.init(Cipher.ENCRYPT_MODE, secretKey)

    return c.doFinal(this)

}

/**
 * Simple one-shot decryption
 * @param cipherType Cipher type to use
 * @param key 128bit key
 */
fun ByteArray.decrypt(cipherType: CipherType, key: ByteArray): ByteArray {
    val secretKey = cipherType.createKey(key)

    val c = cipherType.getInstance()
    c.init(Cipher.DECRYPT_MODE, secretKey)

    return c.doFinal(this)
}