package sx.net

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info
import sx.net.UdpDiscoveryService
import java.io.Serializable
import java.net.InetAddress
import sx.Copyable

/**
 * Created by masc on 22/08/16.
 */
class UdpDiscoveryServiceTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Ignore
    @Test
    fun testService() {
        /**
         * Exxample info class
         */
        data class Test(val info: String) : Copyable<Test>, Serializable {
            constructor() : this(info = "") { }
            override fun copyInstance(): Test { return this.copy() }
        }

        val ds = UdpDiscoveryService<Test>(20000)

        ds.rxOnUpdate.subscribe {
            log.info("Event ${it}")
            ds.directory.forEach {
                log.info("HOST ${it}")
            }
        }

        ds.start()
        Thread.sleep(5000)
        ds.updateInfo(Test("123"))
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}