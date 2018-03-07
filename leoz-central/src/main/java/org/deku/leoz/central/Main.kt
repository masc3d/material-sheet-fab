package org.deku.leoz.central

import com.github.salomonbrys.kodein.Kodein
import org.deku.leoz.central.config.ApplicationConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * Spring boot main application class
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 * Created by masc on 28.05.15.
 */
@Configuration("central.main")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
class Main : org.deku.leoz.node.Main() {
    companion object {
        private val log = LoggerFactory.getLogger(Main::class.java)

        /**
         * Main entry point
         * @param args
         * @throws Exception
         */
        @JvmStatic fun main(args: Array<String>) {
            Main().run(args)
        }
    }

    override val modules: List<Kodein.Module> by lazy {
        listOf(
                ApplicationConfiguration.module,
                LogConfiguration.module
        )
    }
}