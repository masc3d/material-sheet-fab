package sx.security

import org.junit.Assert
import org.junit.Test
import sx.text.parseHex
import java.nio.charset.Charset
import javax.crypto.Cipher

/**
 * Created by masc on 02.05.17.
 */
class SecurityTest {
    val KEY = "9448cadc76f2029315eb0daaa92e4900"
    val VALUE = "Test content ioqwue oiqwu eiuqwoe qwoeuowq ueoqwu eioqwu oe uoqwe"

    @Test
    fun testAes() {
        val cipher = CipherType.AES
        val key = cipher.createKey(KEY.parseHex())

        val c = cipher.instance()
        c.init(Cipher.ENCRYPT_MODE, key)

        val encrypted = c.doFinal(VALUE.toByteArray())

        c.init(Cipher.DECRYPT_MODE, key)
        val decrypted = c.doFinal(encrypted)

        Assert.assertEquals(
                VALUE,
                decrypted.toString(charset = Charset.defaultCharset()))
    }
}