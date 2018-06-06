package sx.text

import org.junit.Assert
import org.junit.Test

/**
 * Created by masc on 03.05.17.
 */
class HexTest {
    val bytes = arrayOf(
            0x94, 0x48, 0xca, 0xdc, 0x76, 0xf2, 0x02, 0x93, 0x15, 0xeb, 0x0d, 0xaa, 0xa9, 0x2e, 0x49, 0x00
    )
            .map { it.toByte() }
            .toByteArray()

    val bytesString = "9448cadc76f2029315eb0daaa92e4900"

    @Test
    fun testToHexString() {
        Assert.assertEquals(bytes.toHexString(), bytesString)
    }

    @Test
    fun testParseHex() {
        Assert.assertTrue(bytesString.parseHex().contentEquals(bytes))
    }
}