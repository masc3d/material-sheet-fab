package sx.net

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.Copyable
import sx.io.serialization.Serializable

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
        @Serializable(0x34e2c0f35e25b0)
        data class Info(val type: String) : Copyable<Info> {
            constructor() : this(type = "type-a") { }
            override fun copyInstance(): Info { return this.copy() }
        }

        val ds = UdpDiscoveryService(Info::class.java, 20000)

        ds.rxOnUpdate.subscribe {
            log.info("Event ${it}")
            ds.directory.forEach {
                log.info("HOST ${it}")
            }
        }

        ds.start()
        Thread.sleep(5000)
        ds.updateInfo(Info("type-b"))
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}