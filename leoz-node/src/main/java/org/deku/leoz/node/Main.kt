package org.deku.leoz.node

import com.vaadin.spring.boot.VaadinAutoConfiguration
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.config.LogConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.springframework.boot.Banner
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
        ServerPropertiesAutoConfiguration::class,
        PersistenceConfiguration::class,
        /**
         * Resteasy configuration. Only used for base setup, not fully autowired
         * as we currently prefer to setup the classic resteasy servlet manually
         * @link WebContextInitializer
         */
        ResteasyAutoConfiguration::class,
        /** Flyway database migration setup  */
        //FlywayAutoConfiguration.class
        VaadinAutoConfiguration::class
)
@EnableConfigurationProperties
open class Main {
    companion object {
        private val log = LogFactory.getLog(Main::class.java)

        /**
         * Static main entry point
         * @param args process arguments
         */
        @JvmStatic fun main(args: Array<String>) {
            Main().run(args)
        }

        /**
         * Stop application entry point
         * This one is only used externally, eg. by the service wrapper when running as a service.
         */
        @Suppress("unused_parameter") @JvmStatic fun stop(args: Array<String>) {
            App.instance.shutdown()
        }
    }

    private val app = App.instance

    /**
     * Main instance entry point
     * @param args process arguments
     * */
    protected fun run(args: Array<String>?) {
        try {
            log.trace("Main arguments [${args!!.joinToString(", ")}]")

            // Support for leoz bundle process commandline interface
            val setup = Setup(
                    serviceId = this.app.name,
                    mainClass = this.javaClass)

            val command = setup.parse(args)
            if (command != null) {
                try {
                    // Setup should write to dedicated logfile
                    LogConfiguration.instance.logFile = StorageConfiguration.instance.setupLogFile
                    LogConfiguration.instance.initialize()
                    // Run setup command
                    command.run()
                } catch (e: Exception) {
                    log.error(e.message, e)
                    System.exit(-1)
                } finally {
                    LogConfiguration.instance.close()
                }
            } else {
                // Initialize and start application
                App.instance.initialize()
                SpringApplicationBuilder()
                        .bannerMode(Banner.Mode.OFF)
                        .sources(this.javaClass)
                        .profiles(this.app.profile)
                        .listeners(this.app).run(*args)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
