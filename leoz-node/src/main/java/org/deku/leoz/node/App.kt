package org.deku.leoz.node

import com.google.common.collect.Lists
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.deku.leoz.node.config.StorageConfiguration
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
import sx.JarManifest
import sx.LazyInstance
import sx.platform.JvmUtil
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.Supplier
import kotlin.properties.Delegates

/**
 * Application instance. Performs pre-spring configuration.
 * Created by masc on 30.05.15.
 */
open class App :
        Disposable,
        // Srping won't recognize this as App is not a bean but
        // we'll inject this within web application initializer
        ApplicationContextAware,
        ApplicationListener<ApplicationEvent> {

    companion object {
        /** Logger  */
        private val log = LogFactory.getLog(App::class.java)

        /** Client node profile, activates specific configurations for leoz client nodes  */
        const val PROFILE_CLIENT_NODE = "client-node"

        /** Injectable lazy instance */
        @JvmStatic val injectableInstance = LazyInstance(Supplier { App() })
        /** Convenience accessor */
        @JvmStatic val instance by lazy({ injectableInstance.get() })
    }

    /** c'tor  */
    protected constructor()

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

    /**
     * Application class type.
     * Needs to be overridden in derived classes to reflect the actual derived type, for eg. JarManifest to work
     */
    open val type: Class<out Any>
        get() = App::class.java

    /**
     * Application jar manifest
     */
    private val jarManifest: JarManifest by lazy({
        JarManifest(this.type)
    })

    /**
     * Application version.
     * As the version is read from jar manifest, property will be not aailable when run from anywhere else than jar
     */
    val version: String
        get() = this.jarManifest.implementationVersion

    /**
     * Lock for preventing duplicate processes
     */
    private var bundlePathLock: FileLock by Delegates.notNull()

    /** Application wide Node identity */
    var identity: Identity by Delegates.notNull()

    /** Application wide system information */
    val systemInformation: SystemInformation by lazy {
        SystemInformation.create()
    }

    /**
     * Initializes identity
     * @return Identity
     */
    fun initializeIdentity(recreate: Boolean = false) {
        var identity: Identity? = null

        if (!recreate) {
            // Verify and read existing identity file
            val identityFile = StorageConfiguration.instance.identityConfigurationFile
            if (identityFile.exists()) {
                try {
                    identity = Identity.load(this.systemInformation, identityFile)
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            }
        }

        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create(
                    App.instance.name,
                    this.systemInformation)

            // Store updates/created identity
            try {
                identity.save(StorageConfiguration.instance.identityConfigurationFile)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        this.identity = identity

        // Start authorizer
//        this.authorizationService = AuthorizationService(ActiveMQConfiguration.instance)
//        this.startAuthorization()
    }

    /**
     * Intialize application
     * @param profile Spring profile name
     */
    protected fun initialize(profile: String) {

        if (isInitialized)
            throw IllegalStateException("Application already initialized")
        isInitialized = true

        // Acquire lock on bundle path
        StorageConfiguration.instance.initalize()
        val bundlePath = StorageConfiguration.instance.bundleLockFile
        log.trace("Acquiring lock on bundle path [${bundlePath}]")
        this.bundlePathLock = FileChannel
                .open(bundlePath.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE)
                .lock()

        this.profile = profile

        // Initialize logging
        if (this.profile === PROFILE_CLIENT_NODE) {
            LogConfiguration.instance.jmsAppenderEnabled = true
        }
        LogConfiguration.instance.initialize()
        RsyncConfiguration.initialize()

        log.info("${this.name} [${version}] ${JvmUtil.shortInfoText}")

        // Log system information
        log.info(this.systemInformation)

        // Initialize identity
        this.initializeIdentity()
        log.info(identity)

        // Uncaught threaded exception handler
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                log.error(e.message, e)
                System.exit(-1)
            }
        })

        //region Spring configuration
        run { // Set additional config file location for spring
            val configLocations = ArrayList<URL>()

            // Add local home configuration
            try {
                configLocations.add(URL("file:" + StorageConfiguration.instance.applicationConfigurationFile.toString()))
            } catch (e: MalformedURLException) {
                log.error(e.message, e)
            }

            // Add application.properties from all classpaths
            // TODO: needs refinement, should only read application.properties from specific jars
            try {
                configLocations.addAll(Collections.list(Thread.currentThread().contextClassLoader.getResources("application.yml")))
            } catch (e: IOException) {
                log.error(e.message, e)
            }

            System.setProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY,
                    Lists.reverse(configLocations).asSequence().map({ u -> u.toString() }).joinToString(","))

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(object : Thread("App shutdown hook") {
                override fun run() {
                    log.info("Shutdown hook initiated")
                    this@App.close()
                    log.info("Shutdown hook completed")
                }
            })

        }
        //endregion
    }

    /** Initialize application. supposed to be overridden in derived applications */
    open fun initialize() {
        this.initialize(PROFILE_CLIENT_NODE)
    }

    /** Dispose application resources */
    override fun close() {
        for (d in Lists.reverse(ArrayList(disposables))) {
            Dispose.safely(d)
        }
    }

    /**
     * Shutdown application
     * @param exitCode Exit code
     */
    fun shutdown(exitCode: Int) {
        if (this.isShuttingDown) {
            log.warn("Already shutting down")
            return
        }

        this.isShuttingDown = true
        log.info("Shutting down")

        // Explicitly shutting down spring application context with specific exit code
        if (this.springApplicationContext != null)
            SpringApplication.exit(
                    this.springApplicationContext,
                    ExitCodeGenerator { exitCode })

        // Close application/dispose resources
        this.close()
    }

    fun shutdown() {
        this.shutdown(0)
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.springApplicationContext = applicationContext
    }

    override fun onApplicationEvent(event: ApplicationEvent) {
        log.info("Spring application event: ${event.javaClass.simpleName}")

        if (event is ApplicationEnvironmentPreparedEvent) {
            // Spring resets logging configuration.
            // As we don't want to supply a logging framework specific config file, simply reapplying
            // logging configuration after spring environment has been prepared.
            LogConfiguration.instance.initialize()
        } else if (event is ApplicationPreparedEvent) {
        } else if (event is EmbeddedServletContainerInitializedEvent) {
            // Post spring initialization
        }
    }
}
