package org.deku.leoz.node

import ch.qos.logback.classic.LoggerContext
import com.vaadin.spring.boot.VaadinAutoConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.Banner
import org.springframework.boot.actuate.autoconfigure.EndpointWebMvcAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.JolokiaAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.ManagementServerPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.web.*
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration
import org.springframework.context.annotation.AnnotationBeanNameGenerator
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

        /** Persistence configuration support */
        PersistenceConfiguration::class,

        /**
         * Resteasy configuration. Only used for base setup, not fully autowired
         * as we currently prefer to setup the classic resteasy servlet manually
         * @link WebContextInitializer
         */
        ResteasyAutoConfiguration::class,

        /** Web mvc & dispatcher auto configuration */
        HttpMessageConvertersAutoConfiguration::class,
        WebMvcAutoConfiguration::class,
        DispatcherServletAutoConfiguration::class,

        /** Actuator endpoint configuration, required for automatically dispatching jolokia */
        ManagementServerPropertiesAutoConfiguration::class,
        EndpointWebMvcAutoConfiguration::class,

        /** Jolokia */
        JolokiaAutoConfiguration::class,

        /** Flyway database migration setup  */
        FlywayAutoConfiguration::class,

        /** Vaadin */
        VaadinAutoConfiguration::class
)
@EnableConfigurationProperties
open class Main {
    companion object {
        private val log = LoggerFactory.getLogger(Main::class.java)

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
            try {
                val springApplication = SpringApplicationBuilder()
                        .beanNameGenerator(object : AnnotationBeanNameGenerator() {
                            override fun buildDefaultBeanName(definition: BeanDefinition?): String {
                                // Override the bean name to be fully qualified.
                                // The default behaviour causes issues with classes having the same name in different packages
                                val beanName = definition!!.beanClassName
                                return beanName
                            }
                        })
                        .bannerMode(Banner.Mode.OFF)
                        .sources(this.javaClass)
                        .profiles(this.app.profile)
                        .listeners(this.app)
                        .build()

                springApplication.run(*args)
            } catch(e: Throwable) {
                // In some situations spring will prevent further logging due to turboFilterList, thus clearing it here
                val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
                loggerContext.turboFilterList.clear()
                throw e
            }
        }
    }
}
