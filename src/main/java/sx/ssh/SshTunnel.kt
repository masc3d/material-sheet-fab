package sx.ssh

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.SshdSocketAddress
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * SSH tunnel
 * Created by masc on 22.11.15.
 * @property sshHost SSH host
 * @property sshPort SSH port
 * @property sshUsername SSH username
 * @property sshPassword SSH password
 * @property remotePort Remote port to tunnel to
 * @property localPort Local port, tunnel entrance
 */
class SshTunnel(
        val sshHost: SshHost,
        val remotePort: Int,
        val localPort: Int)
:
        AutoCloseable {
    class AuthenticationException : Exception() {}

    private val log: Log = LogFactory.getLog(this.javaClass)

    private var session: ClientSession? = null

    /**
     * Open SSH tunnel
     */
    @Synchronized fun open() {
        var session = this.session
        log.info("SSH tunneled connection request to [${sshHost}:${remotePort}] via SSH port [${this.sshHost.port}] through [localhost:${this.localPort}]")
        if (session == null || !session.isOpen) {
            this.close()

            log.info("Establishing tunnel connection to [${sshHost}]")
            val ssh = SshClient.setUpDefaultClient()

            ssh.start()

            val sshFuture = ssh.connect(this.sshHost.username, this.sshHost.hostname, this.sshHost.port)

            if (!sshFuture.await(6, TimeUnit.SECONDS))
                throw TimeoutException("Timeout while connecting [${this}]")

            session = sshFuture.await().session

            session.addPasswordIdentity(this.sshHost.password)

            val result = session.auth().await()
            if (result.isFailure)
                throw AuthenticationException()

            session.startLocalPortForwarding(
                    SshdSocketAddress("localhost", this.localPort),
                    SshdSocketAddress("localhost", this.remotePort))

            this.session = session
            log.info("Established tunnel connection to [${sshHost}]")
        }
    }

    /**
     * Close tunnel
     */
    override fun close() {
        var session = this.session
        if (session != null) {
            log.info("Closing tunnel connection to [${sshHost}]")
            try {
                session.close(false).await()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
            this.session = null
        }
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(host=${this.sshHost}, port=${this.remotePort})"
    }
}