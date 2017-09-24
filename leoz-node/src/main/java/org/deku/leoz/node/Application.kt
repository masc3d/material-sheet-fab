package org.deku.leoz.node

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.collect.Lists
import org.deku.leoz.identity.DesktopIdentityFactory
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.identity.Identity
import org.deku.leoz.node.config.LogConfiguration
import org.slf4j.LoggerFactory
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
import sx.io.ProcessLockFile
import sx.logging.slf4j.info
import sx.platform.JvmUtil
import java.net.URL
import java.util.*
import kotlin.properties.Delegates

/**
 * Application instance. Performs pre-spring configuration.
 * Created by masc on 30.05.15.
 */
open class Application :
        Disposable,
        // Srping won't recognize this as App is not a bean but
        // we'll inject this within web application initializer
        ApplicationContextAware,
        ApplicationListener<ApplicationEvent> {

    companion object {
        /** Logger  */
        private val log = LoggerFactory.getLogger(Application::class.java)

        /** Client node profile, activates specific configurations for leoz client nodes  */
        const val PROFILE_CLIENT_NODE = "client-node"

        private val SPRING_DEVTOOLS_RESTART_ENABLED = "spring.devtools.restart.enabled"
        private val SPRING_DEVTOOLS_LIVERELOAD_ENABLED = "spring.devtools.livereload.enabled"

        /**
         * Spring boot devtools restart/live reload enabled
         */
        var springBootDevToolsEnabled: Boolean?
            get() {
                return System.getProperty(SPRING_DEVTOOLS_RESTART_ENABLED)?.toBoolean()
            }
            set(value) {
                if (value != null) {
                    System.setProperty(SPRING_DEVTOOLS_RESTART_ENABLED, "${value}")
                    System.setProperty(SPRING_DEVTOOLS_LIVERELOAD_ENABLED, "${value}")
                }
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

    /**
     * Application class type.
     * Needs to be overridden in derived classes to reflect the actual derived type, for eg. JarManifest to work
     */
    open val type: Class<out Any>
        get() = Application::class.java

    /**
     * Application bundle type
     */
    open val bundleType: BundleType
        get() = BundleType.LEOZ_NODE

    /**
     * Application jar manifest
     */
    private val jarManifest: JarManifest by lazy {
        JarManifest(this.type)
    }

    /**
     * Application version.
     * As the version is read from jar manifest, property will be not aailable when run from anywhere else than jar
     */
    val version: String
        get() = this.jarManifest.implementationVersion

    /**
     * Lock for preventing duplicate processes
     */
    private var processLockFile: ProcessLockFile by Delegates.notNull()

    /** Application wide Node identity */
    var identity: Identity by Delegates.notNull()
        private set

    /** Application wide system information */
    val systemInformation: SystemInformation by lazy {
        SystemInformation.create()
    }

    /** Storage configuration */
    private val storage: Storage by Kodein.global.lazy.instance()
    /** Log configuration */
    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()

    /**
     * Initializes identity
     * @return Identity
     */
    fun initializeIdentity(recreate: Boolean = false) {
        var identity: Identity? = null

        if (!recreate) {
            // Verify and read existing identity file
            val identityFile = this.storage.identityConfigurationFile
            if (identityFile.exists()) {
                try {
                    identity = Identity.load(identityFile)
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            }
        }

        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = DesktopIdentityFactory(
                    name = this.name,
                    systemInformation = this.systemInformation
            )
                    .create()

            // Store updates/created identity
            try {
                identity.save(this.storage.identityConfigurationFile)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        this.identity = identity

    }

    /**
     * Intialize application
     * @param profile Spring profile name
     */
    protected fun initialize(profile: String) {

        if (isInitialized)
            throw IllegalStateException("Application already initialized")
        isInitialized = true

        // Acquire lock on bundle path (except if spring dev tools restart/live reload is active)
        if (!(Application.springBootDevToolsEnabled ?: false)) {
            log.trace("Acquiring lock on bundle path [${this.storage.bundleLockFile}]")
            this.processLockFile = ProcessLockFile(
                    lockFile = this.storage.bundleLockFile,
                    pidFile = this.storage.bundlePidFile)

            if (!this.processLockFile.isOwner) {
                log.info("Waiting for lock on [${this.processLockFile}]")
                this.processLockFile.waitForLock(
                        writeCurrentProcessPid = true)
            }

            log.info("Acquired lock [${this.processLockFile}}")
        }

        this.profile = profile

        // Initialize logging
        if (this.profile === PROFILE_CLIENT_NODE) {
            logConfiguration.jmsAppenderEnabled = true
        }
        logConfiguration.initialize()

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
        run {
            // Set additional config file location for spring
            val configLocations = arrayListOf<URL>()

            // Add application.properties from other classpaths/jars
            configLocations.addAll(
                    Thread.currentThread().contextClassLoader.getResources("application.yml")
                            .toList()
                            // When application was derived from, the derived jar configuration will always be delivered first, thus reversing the order of embdded configurations
                            .reversed())

            // Add local home configuration
            configLocations.add(this.storage.applicationConfigurationFile.toURI().toURL())

            // Log configuration locations
            configLocations.forEach {
                log.info("Using configuration location [${it}]")
            }

            System.setProperty(
                    ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY,
                    configLocations.map { u -> u.toString() }.joinToString(","))

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(object : Thread("App shutdown hook") {
                override fun run() {
                    log.info("Shutdown hook initiated")
                    this@Application.close()
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
        log.trace("Spring application event: ${event.javaClass.simpleName}")

        if (event is ApplicationEnvironmentPreparedEvent) {
            // Spring resets logging configuration.
            // As we don't want to supply a logging framework specific config file, simply reapplying
            // logging configuration after spring environment has been prepared.
            logConfiguration.initialize()
        } else if (event is ApplicationPreparedEvent) {
        } else if (event is EmbeddedServletContainerInitializedEvent) {
            // Post spring initialization
        }
    }
}
