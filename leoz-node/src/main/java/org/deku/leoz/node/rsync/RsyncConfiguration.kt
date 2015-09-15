package org.deku.leoz.node.rsync

import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.LocalStorage
import org.deku.leoz.node.LogConfiguration
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
public open class RsyncConfiguration {
    private val log = LogFactory.getLog(javaClass<RsyncConfiguration>())

    /** Rsync server instance */
    private var rsyncServer: RsyncServer by Delegates.notNull()

    /** Server properties holder */
    public inner class Server {
        public var port: Int? = null
    }
    public var server: Server = Server()

    /** Client properties holder */
    public inner class Client {
        public var port: Int? = null
    }
    public var client: Client = Client()

    @PostConstruct
    public fun initialize() {
        // Initialize rsync executable path
        Rsync.executableBaseFilename = "leoz-rsync"
        log.info(Rsync.executableFile)

        // Rsync configuration
        val config = RsyncServer.Configuration()
        config.port = this.server.port
        config.logFile = File(LocalStorage.instance().logDirectory, "rsyncd.log")

        // Initialize and start server
        rsyncServer = RsyncServer(LocalStorage.instance().etcDirectory, config)
        rsyncServer.onTermination = { e ->
            if (e != null) log.error(e.getMessage(), e)
        }
        this.rsyncServer.start()
    }

    @PreDestroy
    public fun shutdown() {
        this.rsyncServer.dispose()
    }
}