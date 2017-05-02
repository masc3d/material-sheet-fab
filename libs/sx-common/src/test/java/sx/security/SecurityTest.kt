package sx.security

import org.junit.Assert
import org.junit.Test
import sx.text.toHexString
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by masc on 02.05.17.
 */
class SecurityTest {
    val KEY = "ADBSJHJS12547896".toByteArray()
    val VALUE = "Test content ioqwue oiqwu eiuqwoe qwoeuowq ueoqwu eioqwu oe uoqwe"

    @Test
    fun testAes() {
        val cipher = CipherType.AES
        val key = cipher.createKey(KEY)

        val c = cipher.getInstance()
        c.init(Cipher.ENCRYPT_MODE, key)

        val encrypted = c.doFinal(VALUE.toByteArray())

        c.init(Cipher.DECRYPT_MODE, key)
        val decrypted = c.doFinal(encrypted)

        Assert.assertEquals(
                VALUE,
                decrypted.toString(charset = Charset.defaultCharset()))
    }
}