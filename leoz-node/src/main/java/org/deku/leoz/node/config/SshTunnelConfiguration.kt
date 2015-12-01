package org.deku.leoz.node.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.ssh.SshHost
import sx.ssh.SshTunnelProvider
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

/**
 * Leoz SSH tunnel configuration
 * Created by masc on 01.12.15.
 */
@Configuration
@Lazy(false)
open class SshTunnelConfiguration {
    /**
     * SSH tunnel configuration properties
     */
    @Named
    @ConfigurationProperties(prefix = "ssh.tunnel")
    class Settings {
        var localPortRangeStart: Int by Delegates.notNull()
        var localPortRangeEnd: Int by Delegates.notNull()

        var hosts: Map<String, Host> = HashMap()

        class Host {
            var hostname: String by Delegates.notNull()
            var port: Int by Delegates.notNull()
            var username: String by Delegates.notNull()
            var password: String by Delegates.notNull()
        }
    }

    @Inject
    lateinit var settings: Settings

    /**
     * SSH tunnel provider
     * */
    @Bean
    open fun tunnelProvider(): SshTunnelProvider {
        // Create SSH tunnel provider from settings/application.properties
        return SshTunnelProvider(
                localPortRange = IntRange(
                        this.settings.localPortRangeStart!!,
                        this.settings.localPortRangeEnd!!),
                sshHosts = *this.settings.hosts.values.map {
                    SshHost(
                            hostname = it.hostname!!,
                            port = it.port!!,
                            username = it.username!!,
                            password = it.password!!)
                }.toTypedArray())
    }

    @PostConstruct
    fun onInitialize() {
    }
}