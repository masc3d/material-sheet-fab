package sx.net

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info
import sx.net.UdpDiscoveryService

/**
 * Created by masc on 22/08/16.
 */
class UdpDiscoveryServiceTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Ignore
    @Test
    fun testService() {
        val ds = UdpDiscoveryService(20000)

        ds.start()
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}