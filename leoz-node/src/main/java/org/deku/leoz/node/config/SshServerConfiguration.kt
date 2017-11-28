package org.deku.leoz.node.config

//import org.apache.sshd.common.io.nio2.Nio2Acceptor2
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.common.io.mina.MinaServiceFactoryFactory
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import org.apache.sshd.server.forward.DirectTcpipFactory
import org.apache.sshd.server.forward.DirectTcpipFactoryFixed
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.deku.leoz.config.SshConfiguration
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.io.File
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * SSH server configuration
 * Created by masc on 17.11.15.
 */
@Configuration
@Lazy(false)
open class SshServerConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val sshServer: SshServer

    companion object {
        const val DEFAULT_PORT = 13003
    }

    init {
        this.sshServer = SshServer.setUpDefaultServer()
    }

    @Inject
    private lateinit var storage: Storage

    @PostConstruct
    fun onInitialize() {
        val sshd = this.sshServer

        sshd.port = SshServerConfiguration.DEFAULT_PORT

        sshd.ioServiceFactoryFactory = MinaServiceFactoryFactory()

        sshd.keyPairProvider = SimpleGeneratorHostKeyProvider(
                File(storage.sshDataDirectory, "hostkey.ser"))

        sshd.forwardingFilter = AcceptAllForwardingFilter()

        sshd.passwordAuthenticator = object : PasswordAuthenticator {
            override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {
                return SshConfiguration.USERNAME == username &&
                        SshConfiguration.PASSWORD == password
            }
        }

        // Setup channel factories
        val channelFactories = ArrayList<NamedFactory<Channel>>()
        // Add all factories which don't need customization
        channelFactories.addAll(sshd.channelFactories.filter { cf -> !(cf is DirectTcpipFactory) })
        // Add customized DirectTcpIpFactory, closing SSH session when the tunneled connection breaks.
        // Fixes rsync client stalling over SSH tunnel in case of specific errors, eg. directory doesn't exist
        channelFactories.add(DirectTcpipFactoryFixed())

        sshd.channelFactories = channelFactories

        log.info("Starting ssh server")
        this.sshServer.start()
    }

    @PreDestroy
    fun onDestruction() {
        this.sshServer.stop()
    }
}