package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.ui.bridge.LeoBridge
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService

/**
 * Created by masc on 22/11/2016.
 */
class LeoBridgeConfiguration {
    companion object {
        val log = LoggerFactory.getLogger(LeoBridgeConfiguration::class.java)

        val module = Kodein.Module {
            /** Leo bridge */
            bind<LeoBridge>() with eagerSingleton {
                val leoBridge = LeoBridge()
                val executorService: ExecutorService = instance()
                executorService.submit {
                    log.info("Staring LeoBridge")
                    leoBridge.start()
                }
                leoBridge
            }
        }
    }
}