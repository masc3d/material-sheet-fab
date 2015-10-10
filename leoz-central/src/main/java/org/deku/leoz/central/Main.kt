package org.deku.leoz.central

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.config.DatabaseSyncConfiguration
import org.deku.leoz.central.config.EntitySyncConfiguration
import org.deku.leoz.central.config.MessageListenerConfiguration
import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.util.function.Supplier

/**
 * Spring boot main application class
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
@Import(
        PersistenceConfiguration::class,
        EntitySyncConfiguration::class,
        DatabaseSyncConfiguration::class,
        MessageListenerConfiguration::class)
class Main : org.deku.leoz.node.Main() {
    companion object {
        private val log = LogFactory.getLog(Main::class.java)

        /**
         * Main entry point
         * @param args
         * @throws Exception
         */
        @JvmStatic fun main(args: Array<String>) {
            // Manually inject derived app instance into base class singleton
            org.deku.leoz.node.App.inject(
                    Supplier<org.deku.leoz.node.App> { App() })

            Main().run(args)
        }
    }
}