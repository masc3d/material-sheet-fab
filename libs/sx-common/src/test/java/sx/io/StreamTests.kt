package sx.io

import org.junit.Assert
import org.junit.Test
import sx.collections.chunked
import java.io.ByteArrayInputStream
import java.util.*

/**
 * Stream tests
 * Created by masc on 17.12.17.
 */
class StreamTests {
    @Test
    fun testToSequence() {
        val chunks = 5
        val maxSize = 1000
        val b = ByteArray(chunks * maxSize + 500)
        Random().nextBytes(b)

        val r = ByteArrayInputStream(b).toSequence(maxSize).toList()
        Assert.assertEquals(chunks + 1, r.size)
        r.forEachIndexed { index, bytes ->
            Assert.assertArrayEquals(
                    b.sliceArray((index * maxSize).until((index * maxSize) + bytes.size)),
                    bytes)
        }
    }
}