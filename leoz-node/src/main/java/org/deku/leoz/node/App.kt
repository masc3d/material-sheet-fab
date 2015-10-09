package org.deku.leoz.node

import com.google.common.collect.Lists
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.JarManifest
import org.deku.leoz.node.config.IdentityConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.springframework.beans.BeansException
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.config.ConfigFileApplicationListener
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import sx.Disposable
import sx.Dispose
import sx.LazyInstance
import sx.jms.embedded.activemq.ActiveMQBroker

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.function.Supplier

/**
 * Created by masc on 30.05.15.
 */
open class App
/** c'tor  */
protected constructor() : Disposable, // Srping won't recognize this as App is not a bean but
        // we'll inject this within web application initializer
        ApplicationContextAware, ApplicationListener<ApplicationEvent> {

    companion object {
        /** Logger  */
        private val log = LogFactory.getLog(App::class.java)

        /** Client node profile, activates specific configurations for leoz client nodes  */
        const val PROFILE_CLIENT_NODE = "client-node"

        //region Singleton
        private val instance = LazyInstance(Supplier { App() })

        /**
         * Singleton instance
         * @return
         */
        @JvmStatic fun instance(): App {
            return instance.get()
        }

        /**
         * Static injection
         * @param supplier
         */
        @JvmStatic fun inject(supplier: Supplier<App>) {
            instance.set(supplier)
        }
    }

    /** The spring application context */
    var springApplicationContext: ApplicationContext? = null
        private set

    /** Disposable instances not managed by spring  */
    private val disposables = ArrayList<Disposable>()

    /** Indicates if app is shutting down */
    @Volatile private var isShuttingDown: Boolean = false
    /** Indicates if app has been initialized */
    @Volatile private var isInitialized: Boolean = false

    /** Spring application profile */
    var profile: String? = null
        private set

    /** Application name  */
    open val name: String
        get() = "leoz-node"

    /** Application jar manifest */
    val jarManifest: JarManifest by lazy({
        JarManifest(this.javaClass)
    })

    /**
     * Intialize application
     * @param profile Spring profile name
     */
    protected fun initialize(profile: String) {

        if (isInitialized)
            throw IllegalStateException("Application already initialized")
        isInitialized = true

        this.profile = profile

        // Initialize logging
        if (this.profile === PROFILE_CLIENT_NODE) {
            LogConfiguration.instance().jmsAppenderEnabled = true
        }
        LogConfiguration.instance().initialize()
        disposables.add(LogConfiguration.instance())

        log.info("Leoz node initialize")

        // Uncaught threaded exception handler
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                log.error(e.getMessage(), e)
                System.exit(-1)
            }
        })

        //region Spring configuration
        run { // Set additional config file location for spring
            val configLocations = ArrayList<URL>()

            // Add local home configuration
            try {
                configLocations.add(URL("file:" + LocalStorage.instance.applicationConfigurationFile.toString()))
            } catch (e: MalformedURLException) {
                log.error(e.getMessage(), e)
            }

            // Add application.properties from all classpaths
            // TODO: needs refinement, should only read application.properties from specific jars
            try {
                configLocations.addAll(Collections.list(Thread.currentThread().contextClassLoader.getResources("application.properties")))
            } catch (e: IOException) {
                log.error(e.getMessage(), e)
            }

            System.setProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY,
                    Lists.reverse(configLocations).asSequence().map({ u -> u.toString() }).joinToString(","))
        }
        //endregion

        Runtime.getRuntime().addShutdownHook(object : Thread("App shutdown hook") {
            override fun run() {
                log.info("Shutdown hook initiated")
                App.instance().dispose()
                log.info("Shutdown hook completed")
            }
        })
    }

    /** Initialize application. supposed to be overridden in derived applications */
    open fun initialize() {
        this.initialize(PROFILE_CLIENT_NODE)
    }

    /** Dispose application resources */
    override fun dispose() {
        for (d in Lists.reverse(ArrayList(disposables))) {
            Dispose.safely(d)
        }
    }

    /**
     * Shutdown application
     * @param exitCode Exit code
     */
    fun shutdown(exitCode: Int) {
        if (isShuttingDown) {
            log.warn("Already shutting down")
            return
        }

        isShuttingDown = true
        log.info("Shutting down")
        if (springApplicationContext != null)
            SpringApplication.exit(springApplicationContext, ExitCodeGenerator { exitCode })
        App.instance().dispose()
    }

    fun shutdown() {
        this.shutdown(0)
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        springApplicationContext = applicationContext
    }

    override fun onApplicationEvent(event: ApplicationEvent) {
        log.info("Spring application event: ${event.javaClass.simpleName}")

        if (event is ApplicationEnvironmentPreparedEvent) {
            // Spring resets logging configuration.
            // As we don't want to supply a logging framework specific config file, simply reapplying
            // logging configuration after spring environment has been prepared.
            LogConfiguration.instance().initialize()
        } else if (event is ApplicationPreparedEvent) {
        } else if (event is EmbeddedServletContainerInitializedEvent) {
            // Post spring initialization
        }
    }
}
