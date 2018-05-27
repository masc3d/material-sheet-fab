package org.deku.leoz.node.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.ssh.SshHost
import sx.ssh.SshTunnelProvider
import java.time.Duration
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Leoz SSH tunnel configuration
 * Created by masc on 01.12.15.
 */
@Configuration
@Lazy(false)
class SshTunnelConfiguration {
    /**
     * SSH tunnel configuration properties
     */
    @Configuration
    @ConfigurationProperties(prefix = "ssh.tunnel")
    class Settings {
        var localPortRangeStart: Int by Delegates.notNull()
        var localPortRangeEnd: Int by Delegates.notNull()

        /** Default username */
        var username: String? = null
        /** Defauzlt password */
        var password: String? = null
        /** Default port */
        var port: Int? = 0

        var hosts: Map<String, Host> = HashMap()

        class Host {
            var hostname: String by Delegates.notNull()
            var port: Int by Delegates.notNull()
            var username: String by Delegates.notNull()
            var password: String by Delegates.notNull()
        }
    }

    @Inject
    private lateinit var settings: Settings

    /**
     * SSH tunnel provider
     * */
    @get:Bean
    val tunnelProvider: SshTunnelProvider
        get() {
            val sshHosts = this.settings.hosts.values.map {
                SshHost(
                        hostname = it.hostname,
                        port = it.port,
                        username = it.username,
                        password = it.password)
            }.toMutableList()

            val defaultUsername = this.settings.username
            val defaultPassword = this.settings.password
            val defaultPort = this.settings.port

            if (defaultUsername != null && defaultPassword != null && defaultPort != null) {
                sshHosts.add(SshHost(hostname = "",
                        username = defaultUsername,
                        password = defaultPassword,
                        port = defaultPort))
            }

            // Create SSH tunnel provider from settings/application.properties
            val s = SshTunnelProvider(
                    localPortRange = IntRange(
                            this.settings.localPortRangeStart,
                            this.settings.localPortRangeEnd),
                    sshHosts = *sshHosts.toTypedArray())

            s.idleTimeout = Duration.ofMinutes(3)
            return s
        }

    @PostConstruct
    fun onInitialize() {
    }
}