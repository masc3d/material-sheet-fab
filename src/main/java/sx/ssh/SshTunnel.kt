package sx.ssh

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.SshdSocketAddress

/**
 * Coordinates ssh tunneled connections through localhost
 * Created by masc on 22.11.15.
 * @property portRange Local port range
 */
class SshTunnel(
        val host: String,
        val port: Int,
        val remoteTunnelPort: Int,
        val localTunnelPort: Int,
        val userName: String,
        val password: String)
:
        AutoCloseable {
    class AuthenticationException : Exception() {}

    private val log: Log = LogFactory.getLog(this.javaClass)

    private var requestCount: Int = 0
    private var session: ClientSession? = null

    /**
     * Request tunnel connection.
     * Establishes the tunnel connection if it's not been established yet and
     * increases the request count for this tunnel.
     */
    @Synchronized fun request() {
        var session = this.session
        log.info("SSH tunneled connection request to [${host}:${remoteTunnelPort}] via SSH port [${this.port}] through [localhost:${this.localTunnelPort}]")
        if (session == null || !session.isOpen) {
            this.close()

            log.info("Establishing tunnel connection to [${host}]")
            val ssh = SshClient.setUpDefaultClient()

            ssh.start()

            session = ssh.connect(this.userName, this.host, this.port)
                    .await()
                    .session
            session.addPasswordIdentity(this.password)

            val result = session.auth().await()
            if (result.isFailure)
                throw AuthenticationException()

            session.startLocalPortForwarding(
                    SshdSocketAddress("localhost", this.localTunnelPort),
                    SshdSocketAddress("localhost", this.remoteTunnelPort))

            this.session = session
            log.info("Established tunnel connection to [${host}]")
        }

        requestCount++
        log.info("Increased tunnel request count [${this.requestCount}} for [${this.host}]")
    }

    /**
     * Release tunnel connection.
     * Decreases request count, the tunnel will be closed when the request reaches zero.
     */
    @Synchronized fun release() {
        if (requestCount > 0) {
            requestCount--
            log.info("Decreased tunnel request count [${this.requestCount}} for [${this.host}]")
        }

        if (requestCount == 0) {
            this.close()
        }
    }

    override fun close() {
        var session = this.session
        if (session != null) {
            log.info("Closing tunnel connection to [${host}]")
            try {
                session.close(false).await()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
            this.session = null
        }
    }
}