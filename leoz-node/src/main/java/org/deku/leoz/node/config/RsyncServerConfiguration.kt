package org.deku.leoz.node.config

import org.deku.leoz.config.RsyncConfiguration
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.logging.slf4j.info
import sx.rsync.Rsync
import sx.rsync.RsyncServer
import java.io.File
import java.net.InetAddress
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

/**
 * Rsync configuration.
 * Initializes server and provides client instances.
 * Created by masc on 15.09.15.
 */
@Named
@Configuration
@Lazy(false)
open class RsyncServerConfiguration {
    /** Server properties holder */
    @Configuration
    @ConfigurationProperties(prefix = "rsync.server")
    open class Settings {
        var port: Int? = null
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var settings: Settings

    /** Rsync server instance */
    private var rsyncServer: RsyncServer by Delegates.notNull()

    private val storageConfiguration by lazy({ StorageConfiguration.instance })

    /**
     * Pulls in all application wide rsync modules
     */
    @Inject
    lateinit var rsyncModules: List<Rsync.Module>

    @PostConstruct
    fun onInitialize() {
        // Initialize rsync executable path
        try {
            log.info(Rsync.executable.file)

            // Rsync configuration
            val config = RsyncServer.Configuration()
            // Limit connections to loopback interface. Rsync server is only reachable via (ssh) tunneling
            config.address = InetAddress.getLoopbackAddress().hostAddress
            config.port = this.settings.port
            config.logFile = File(this.storageConfiguration.logDirectory, "leoz-rsyncd.log")
            config.reverseLookup = false
            config.forwardLookup = false

            // Users
            var user = Rsync.User(RsyncConfiguration.USERNAME, RsyncConfiguration.PASSWORD)

            // Bundles module
            for (module in this.rsyncModules) {
                module.permissions.put(user, Rsync.Permission.READWRITE)
                config.modules.add(module)
            }

            // Initialize and start server
            rsyncServer = RsyncServer(this.storageConfiguration.etcDirectory, config)
            rsyncServer.onTermination = { e ->
                if (e != null) log.error(e.message, e)
            }

            // Start rsync server
            this.rsyncServer.start()

        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    @PreDestroy
    fun shutdown() {
        this.rsyncServer.close()
    }
}