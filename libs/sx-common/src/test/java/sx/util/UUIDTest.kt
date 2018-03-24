package sx.util

import org.junit.Test
import org.slf4j.LoggerFactory
import sx.log.slf4j.debug
import sx.log.slf4j.trace
import sx.text.toHexString
import java.util.*

/**
 * Created by masc on 24.03.18.
 */
class UUIDTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testUUID() {
        val uid = UUID.randomUUID()

        log.debug {
            "${uid} ${uid.mostSignificantBits.shr(8*4).toInt().toHexString()}"
        }
    }
}