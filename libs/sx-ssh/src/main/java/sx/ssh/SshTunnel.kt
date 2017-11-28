package sx.ssh

import org.apache.sshd.client.ClientBuilder
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory
import org.apache.sshd.common.session.Session
import org.apache.sshd.common.session.SessionListener
import org.apache.sshd.common.util.net.SshdSocketAddress
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * SSH tunnel
 * Created by masc on 22.11.15.
 * @property sshHost SSH host
 * @property remotePort Remote port to tunnel to
 * @property localPort Local port, tunnel entrance
 * @property connectionTimeout SSH connection timeout
 * @property idleTimeout SSH idle timeout
 * @property onClosed Callback on session close
 */
class SshTunnel(
        val sshHost: SshHost,
        val remotePort: Int,
        val localPort: Int,
        val connectionTimeout: Duration = Duration.ofSeconds(10),
        val idleTimeout: Duration = Duration.ofSeconds(30),
        val onClosed: (sshTunnel: SshTunnel) -> Unit = { },
        val onCreated: (sshTunnel: SshTunnel) -> Unit = { })
:
        AutoCloseable {

    /**
     * SSH tunnel authentication exception
     */
    class AuthenticationException : Exception()

    /** Logger */
    private val log = LoggerFactory.getLogger(this.javaClass)
    /** Sync object */
    private val sync = Object()
    /** SSH client session */
    private var session: ClientSession? = null

    /**
     * SSH session listener
     */
    private val sessionListener = object : SessionListener {
        override fun sessionException(session: Session?, t: Throwable?) {
            if (t != null)
                log.error(t.message, t)
        }

        override fun sessionClosed(session: Session?) {
            log.info("SSH session/tunnel closed [${this@SshTunnel}]")
            this@SshTunnel.close()
            this@SshTunnel.onClosed(this@SshTunnel)
        }

        override fun sessionCreated(session: Session?) {
            log.info("SSH session created [${this@SshTunnel}]")
            this@SshTunnel.onCreated(this@SshTunnel)
        }

        override fun sessionEvent(session: Session?, event: SessionListener.Event?) {
            //log.info("SSH session event [${event}] [${this@SshTunnel}]")
        }
    }

    /**
     * Open SSH tunnel
     */
    fun open() {
        synchronized(sync) {
            var session = this.session
            log.debug("SSH tunneled connection request to [${sshHost}:${remotePort}] via SSH port [${this.sshHost.port}] through [localhost:${this.localPort}]")
            if (session == null || !session.isOpen) {
                this.close()

                log.info("Establishing tunnel connection [${this}]")
                val ssh = ClientBuilder.builder()
                        // Don't parse/use host/user .ssh/config and the keys therein by providing empty host config resolver
                        // Not doing this will eg. make any connection fail if a user has one or more passphrase protected keys
                        // Apart from that we don't want any user specific keys or configuration
                        .hostConfigEntryResolver(HostConfigEntryResolver.EMPTY)
                        .build()

                ssh.ioServiceFactoryFactory = Nio2ServiceFactoryFactory()

                // Set properties
                ssh.properties.set(SshClient.IDLE_TIMEOUT, idleTimeout.toMillis())

                // Start client
                ssh.start()

                // Connect with timeout support
                val sshFuture = ssh.connect(
                        this.sshHost.username,
                        this.sshHost.hostname,
                        this.sshHost.port)

                if (!sshFuture.await(connectionTimeout.toMillis(), TimeUnit.MILLISECONDS))
                    throw TimeoutException("Timeout while connecting [${this}]")

                sshFuture.await()
                session = sshFuture.session
                this.session = session

                // Prepare session and authenticate
                session.addSessionListener(this.sessionListener)
                session.addPasswordIdentity(this.sshHost.password)

                val authFuture = session.auth()
                authFuture.await()
                if (authFuture.isFailure)
                    throw AuthenticationException()

                // Start port forwarding for tunneled connections
                session.startLocalPortForwarding(
                        SshdSocketAddress("localhost", this.localPort),
                        SshdSocketAddress("localhost", this.remotePort))

                log.info("Established tunnel connection to [${sshHost}]")
            }
        }
    }

    /**
     * Close tunnel
     */
    override fun close() {
        synchronized(sync) {
            var session = this.session

            if (session != null) {
                try {
                    if (!session.isClosed && !session.isClosing) {
                        session.close(false).await()
                    }
                } catch(e: Exception) {
                    log.error(e.message, e)
                } finally {
                    this.session = null
                }
            }
        }
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(host=${this.sshHost}, port=${this.remotePort})"
    }
}