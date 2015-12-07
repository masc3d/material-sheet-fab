package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.common.future.CloseFuture
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import org.apache.sshd.server.forward.DirectTcpipFactory
import org.apache.sshd.server.forward.TcpipServerChannel
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.deku.leoz.config.SshConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.io.File
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by masc on 17.11.15.
 */
@Configuration
@Lazy(false)
open class SshServerConfiguration {
    private val log = LogFactory.getLog(this.javaClass)
    private val sshServer: SshServer

    init {
        this.sshServer = SshServer.setUpDefaultServer()
    }

    @Inject
    lateinit var tc: SshTunnelConfiguration

    @PostConstruct
    fun onInitialize() {
        val sshd = this.sshServer

        sshd.setPort(13003)
        sshd.setKeyPairProvider(SimpleGeneratorHostKeyProvider(
                File(StorageConfiguration.instance.sshDataDirectory, "hostkey.ser")))

        sshd.tcpipForwardingFilter = AcceptAllForwardingFilter()

        sshd.setPasswordAuthenticator(object : PasswordAuthenticator {
            override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {
                return SshConfiguration.USERNAME.equals(username) &&
                        SshConfiguration.PASSWORD.equals(password);
            }
        });

        // Setup channel factories
        val channelFactories = ArrayList<NamedFactory<Channel>>()
        // Add all factories which don't need customization
        channelFactories.addAll(sshd.channelFactories.filter { cf -> !(cf is DirectTcpipFactory) })
        // Add customized DirectTcpIpFactory, closing SSH session when the tunneled connection breaks.
        // Fixes rsync client stalling over SSH tunnel in case of specific errors, eg. directory doesn't exist
        channelFactories.add(object : DirectTcpipFactory() {
            override fun create(): Channel? {
                return object : TcpipServerChannel(this.type) {
                    override fun close(immediately: Boolean): CloseFuture? {
                        val closeFuture = super.close(immediately)
                        closeFuture.addListener({
                            // Trigger graceful close of SSH session when tunnel connection closes
                            this.session.close(false)
                        })
                        return closeFuture
                    }
                }
            }
        })
        sshd.channelFactories = channelFactories

        log.info("Starting ssh server")
        this.sshServer.start()
    }

    @PreDestroy
    fun onDestruction() {
        this.sshServer.stop()
    }
}