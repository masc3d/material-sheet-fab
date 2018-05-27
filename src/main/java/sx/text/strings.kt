package sx.text

/**
 * Splits a string into fixed size chunks
 * Created by n3 on 12/07/16.
 */
fun String.splitFixed(size: Int): List<String> {
    val result = arrayListOf<String>()

    var i = 0
    while (i < this.length) {
        result.add(this.substring(i, Math.min(this.length, i + size)))
        i += size
    }

    return result
}