package org.deku.leoz.node.config

import org.apache.sshd.common.FactoryManager
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.common.io.IoAcceptor
import org.apache.sshd.common.io.IoConnector
import org.apache.sshd.common.io.IoHandler
import org.apache.sshd.common.io.IoServiceFactory
import org.apache.sshd.common.io.nio2.Nio2Acceptor2
import org.apache.sshd.common.io.nio2.Nio2Connector
import org.apache.sshd.common.io.nio2.Nio2DefaultIoWriteFuture
import org.apache.sshd.common.io.nio2.Nio2ServiceFactory2
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory
import org.apache.sshd.common.io.nio2.Nio2Session
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
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
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

        sshd.keyPairProvider = SimpleGeneratorHostKeyProvider(
                File(storage.sshDataDirectory, "hostkey.ser"))

        sshd.tcpipForwardingFilter = AcceptAllForwardingFilter()

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

        //region masc20171102. workaround for https://issues.apache.org/jira/browse/SSHD-743
        sshd.ioServiceFactoryFactory = object : Nio2ServiceFactoryFactory() {
            override fun create(manager: FactoryManager?): IoServiceFactory {
                return object : Nio2ServiceFactory2(manager, getExecutorService(), isShutdownOnExit()) {
                    override fun createAcceptor(handler: IoHandler?): IoAcceptor {
                        return object : Nio2Acceptor2(getFactoryManager(), handler, group) {
                            override fun createSocketCompletionHandler(channelsMap: MutableMap<SocketAddress, AsynchronousServerSocketChannel>?, socket: AsynchronousServerSocketChannel?): CompletionHandler<AsynchronousSocketChannel, in SocketAddress> {
                                return object : AcceptCompletionHandler(socket) {
                                    override fun createSession(acceptor: Nio2Acceptor2?, address: SocketAddress?, channel: AsynchronousSocketChannel?, handler: IoHandler?): Nio2Session {
                                        return object : Nio2Session(acceptor, getFactoryManager(), handler, channel) {
                                            override fun handleWriteCycleFailure(future: Nio2DefaultIoWriteFuture?, socket: AsynchronousSocketChannel?, buffer: ByteBuffer?, writeLen: Int, exc: Throwable?, attachment: Any?) {
                                                try {
                                                    super.handleWriteCycleFailure(future, socket, buffer, writeLen, exc, attachment)
                                                } catch (e: Throwable) {
                                                    // Handle leaking exception to prvent process termination
                                                    log.error(e.message, e)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun createConnector(handler: IoHandler?): IoConnector {
                        return object : Nio2Connector(getFactoryManager(), handler, group) {
                            override fun createSession(manager: FactoryManager?, handler: IoHandler?, socket: AsynchronousSocketChannel?): Nio2Session {
                                return object : Nio2Session(this, manager, handler, socket) {
                                    override fun handleWriteCycleFailure(future: Nio2DefaultIoWriteFuture?, socket: AsynchronousSocketChannel?, buffer: ByteBuffer?, writeLen: Int, exc: Throwable?, attachment: Any?) {
                                        try {
                                            super.handleWriteCycleFailure(future, socket, buffer, writeLen, exc, attachment)
                                        } catch (e: Throwable) {
                                            // Handle leaking exception to prvent process termination
                                            log.error(e.message, e)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //endregion

        sshd.channelFactories = channelFactories

        log.info("Starting ssh server")
        this.sshServer.start()
    }

    @PreDestroy
    fun onDestruction() {
        this.sshServer.stop()
    }
}