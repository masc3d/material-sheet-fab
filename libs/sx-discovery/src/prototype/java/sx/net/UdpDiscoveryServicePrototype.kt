package sx.net

import org.junit.Test
import org.slf4j.LoggerFactory
import io.reactivex.rxkotlin.subscribeBy
import org.threeten.bp.Duration
import sx.Copyable
import sx.io.serialization.Serializable
import java.util.concurrent.Executors

/**
 * Created by masc on 22/08/16.
 */
class UdpDiscoveryServicePrototype {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun runService() {
        /**
         * Exxample info class
         */
        @Serializable(0x34e2c0f35e25b0)
        data class Info(val type: String) : Copyable<Info> {
            constructor() : this(type = "type-a") {}

            override fun copyInstance(): Info {
                return this.copy()
            }
        }

        val e = Executors.newScheduledThreadPool(2)

        val ds = UdpDiscoveryService(
                executorService = e,
                infoClass = Info::class.java,
                port = 20000)

//        ds.rxOnUpdate.subscribe {
//            log.info("Event ${it}")
//            ds.directory.forEach {
//                log.info("HOST ${it}")
//            }
//        }

        ds.discoverFirst({ true }, Duration.ofSeconds(3))
                .subscribeBy(
                        onSuccess = {
                            log.info("DISCOVERED ${it}")
                        },
                        onError = {
                            log.error(it.message, it)
                        })


        ds.start()
        Thread.sleep(5000)
        ds.nodeInfo = Info("type-b")
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}