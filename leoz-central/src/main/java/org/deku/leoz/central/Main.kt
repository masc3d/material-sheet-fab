package org.deku.leoz.central

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.config.StorageConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * Spring boot main application class
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
open class Main : org.deku.leoz.node.Main() {
    companion object {
        private val log = LogFactory.getLog(Main::class.java)

        /**
         * Main entry point
         * @param args
         * @throws Exception
         */
        @JvmStatic fun main(args: Array<String>) {
            // Manually inject derived app instance into base class singletons
            org.deku.leoz.node.App.instance.set({ App.instance.get() })
            org.deku.leoz.node.config.StorageConfiguration.instance.set({ StorageConfiguration.instance })

            Main().run(args)
        }
    }
}