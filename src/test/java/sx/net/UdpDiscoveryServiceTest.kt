package sx.net

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info
import sx.net.UdpDiscoveryService
import java.net.InetAddress

/**
 * Created by masc on 22/08/16.
 */
class UdpDiscoveryServiceTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Ignore
    @Test
    fun testService() {
        val ds = UdpDiscoveryService<String>(20000)

        ds.rxOnUpdate.subscribe {
            log.info("Event ${it}")
        }

        ds.start()
//        ds.updateInfo("test, test2")

        Thread.sleep(5000)
        ds.updateInfo("YO")
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}