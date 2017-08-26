package sx.collections

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest
import java.util.*

@Category(StandardTest::class)
class ArrayTests {
    @Test
    fun testByteArrayChunked() {
        val chunks = 5
        val maxSize = 1000
        val b = ByteArray(chunks * maxSize + 500)
        Random().nextBytes(b)

        val r = b.chunked(maxSize)
        Assert.assertEquals(chunks + 1, r.size)
        r.forEachIndexed { index, bytes ->
            Assert.assertArrayEquals(
                    b.sliceArray((index * maxSize).until((index * maxSize) + bytes.size)),
                    bytes)
        }
    }
}