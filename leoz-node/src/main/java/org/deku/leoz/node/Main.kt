package org.deku.leoz.node

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.config.*
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * Spring boot main application class
 * Created by masc on 28.05.15.
 */
@Configuration("node.MainSpringBoot")
@ComponentScan(lazyInit = true)
@Order(Ordered.LOWEST_PRECEDENCE)
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import(
        /** Setups up embedded web server and servlet container  */
        EmbeddedServletContainerAutoConfiguration::class,
        /** Server properties support  */
        ServerPropertiesAutoConfiguration::class, PersistenceConfiguration::class,
        /**
         * Resteasy configuration. Only used for base setup, not fully autowired
         * as we currently prefer to setup the classic resteasy servlet manually
         * @link WebContextInitializer
         */
        ResteasyAutoConfiguration::class,
        /** Flyway database migration setup  */
        //FlywayAutoConfiguration.class,

        /** Leoz embedded broker configuration  */
        ActiveMqConfiguration::class,
        /** Leoz entity sync configuration  */
        EntitySyncConfiguration::class,
        /** Leoz message listener configuration  */
        MessageListenerConfiguration::class,
        /** Rsync configuration  */
        RsyncConfiguration::class)
@EnableConfigurationProperties
open class Main {
    companion object {
        private val log = LogFactory.getLog(Main::class.java)

        /**
         * Static main entry point
         * @param args process arguments
         */
        @JvmStatic open fun main(args: Array<String>) {
            Main().run(args)
        }

        /**
         * Stop application entry point
         * This one is only used externally, eg. by the service wrapper when running as a service.
         */
        @Suppress("unused_parameter") @JvmStatic fun stop(args: Array<String>) {
            App.instance().shutdown()
        }
    }


    /**
     * Main instance entry point
     * @param args process arguments
     * */
     protected fun run(args: Array<String>?) {
        try {
            log.info("Main arguments [${args!!.joinToString(", ")}]")

            // Support for command line parameters, setup commands
            if (args.size() > 0) {
                val command = args[0].toLowerCase().trim()

                var rCommand: Runnable? = null
                when (command) {
                    "install" -> rCommand = Runnable { Setup.instance().install("LeoZ Service", this.javaClass) }
                    "uninstall" -> rCommand = Runnable { Setup.instance().uninstall() }
                    "start" -> rCommand = Runnable { Setup.instance().start() }
                    "stop" -> rCommand = Runnable { Setup.instance().stop() }
                }

                if (rCommand != null) {
                    try {
                        LogConfiguration.instance().initialize()
                        rCommand.run()
                    } catch (e: Exception) {
                        log.error(e.getMessage(), e)
                        System.exit(-1)
                        throw e
                    } finally {
                        LogConfiguration.instance().dispose()
                    }
                    System.exit(0)
                }
            }

            // Initialize and start application
            App.instance().initialize()
            SpringApplicationBuilder()
                    .showBanner(false)
                    .sources(this.javaClass)
                    .profiles(App.instance().profile)
                    .listeners(App.instance()).run(*args)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
