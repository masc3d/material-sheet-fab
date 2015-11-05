package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.RsyncConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.rsync.Rsync
import sx.rsync.RsyncServer
import java.io.File
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Named
import kotlin.properties.Delegates

/**
 * Rsync configuration.
 * Initializes server and provides client instances.
 * Created by masc on 15.09.15.
 */
@Named
@Configuration
@ConfigurationProperties(prefix = "rsync")
@Lazy(false)
open class RsyncConfiguration {
    /** Server properties holder */
    inner class Server {
        var port: Int? = null
    }

    /** Client properties holder */
    inner class Client {
        var port: Int? = null
    }

    private val log = LogFactory.getLog(RsyncConfiguration::class.java)

    // Properties
    var server: Server = Server()
    var client: Client = Client()

    /** Rsync server instance */
    private var rsyncServer: RsyncServer by Delegates.notNull()

    @PostConstruct
    fun initialize() {
        // Initialize rsync executable path
        Rsync.executable.baseFilename = "leoz-rsync"
        try {
            log.info(Rsync.executable.file)

            // Rsync configuration
            val config = RsyncServer.Configuration()
            config.port = this.server.port
            config.logFile = File(StorageConfiguration.instance.logDirectory, "leoz-rsyncd.log")

            // Users
            var user = Rsync.User(RsyncConfiguration.USERNAME, RsyncConfiguration.PASSWORD)

            // Bundles module
            var module = Rsync.Module("bundles", StorageConfiguration.instance.bundleRepositoryDirectory)
            module.permissions.put(user, Rsync.Permission.READWRITE)
            config.modules.add(module)

            // Initialize and start server
            rsyncServer = RsyncServer(StorageConfiguration.instance.etcDirectory, config)
            rsyncServer.onTermination = { e ->
                if (e != null) log.error(e.message, e)
            }
            this.rsyncServer.start()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    @PreDestroy
    fun shutdown() {
        this.rsyncServer.dispose()
    }
}