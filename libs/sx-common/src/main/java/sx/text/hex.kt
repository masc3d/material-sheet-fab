package sx.text

import java.math.BigInteger
import javax.print.attribute.IntegerSyntax

/**
 *  Extension functions to format bytes as Hex values.
 */

/**
 *  Set of chars for a half-byte.
 */
private val CHARS = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 *  Returns the string of two characters representing the HEX value of the byte.
 */
fun Byte.toHexString(): String {
    val i = this.toInt()
    val char2 = CHARS[i and 0x0f]
    val char1 = CHARS[i shr 4 and 0x0f]
    return "$char1$char2"
}

/**
 *  Returns the hex representation of ByteArray data.
 */
fun ByteArray.toHexString(): String {
    val builder = StringBuilder()
    for (b in this) {
        builder.append(b.toHexString())
    }
    return builder.toString()
}

/**
 * Parses string representation of hex data into byte array
 */
fun String.parseHex(): ByteArray {
    // Using BigInteger for conversion is not the most elegant way, but it's built-in and compatible with android
    val signedBytes = BigInteger(this, 16).toByteArray()

    // BigInteger's toByteArray may prepend a zero (sign) byte
    return if (signedBytes[0].compareTo(0) == 0)
        signedBytes.copyOfRange(1, signedBytes.count())
    else
        signedBytes
}

/** Convert long to hex string */
fun Long.toHexString(): String = java.lang.Long.toHexString(this).padStart(16, '0')

/** Convert integer to hex string */
fun Int.toHexString(): String = java.lang.Integer.toHexString(this).padStart(8, '0')