package sx.ssh

import org.apache.commons.logging.LogFactory
import java.util.*

/**
 * SSH tunnel provider.
 * Managing and providing SSH tunnels with multithreading support.
 * Created by masc on 25.11.15.
 */
class SshTunnelProvider(
        val portRange: IntRange,
        vararg tunnelConfigurations: SshTunnelProvider.TunnelConfiguration
) {
    private val log = LogFactory.getLog(this.javaClass)

    val localPorts = LinkedList<Int>()
    /** SSH tunnel configuration by hostname */
    val tunnelConfigurations = HashMap<String, TunnelConfiguration>()
    /** SSH tunnel maps by composite tunnel key */
    val tunnelRecords = HashMap<TunnelKey, TunnelRecord>()

    /**
     * SSH tunnel configuration
     * @param host SSH host to connect to
     * @param sshPort SSH port
     * @param sshUsername SSH username
     * @param sshPassword SSH password
     */
    data class TunnelConfiguration(
            val host: String,
            val sshPort: Int,
            val sshUsername: String,
            val sshPassword: String) {
    }

    /**
     * SSH tunnel composite key
     */
    data class TunnelKey(
            val threadId: Long,
            val host: String,
            val port: Int) {
    }

    /**
     * SSH tunnel record
     */
    class TunnelRecord(
            val sshTunnel: SshTunnel) {

        var count: Int = 0
    }

    /**
     * SSH tunnel request.
     * Automatically triggers release/closes tunnel when finalized/gc'ed
     */
    open inner class TunnelRequest(
            val key: TunnelKey,
            val localPort: Int)
    :
            AutoCloseable {
        init {
        }

        override fun close() {
            this@SshTunnelProvider.release(this)
        }

        protected fun finalize() {
            this.close()
        }
    }

    init {
        this.localPorts.addAll(this.portRange)
        tunnelConfigurations.forEach { t -> this.tunnelConfigurations.put(t.host, t) }
    }

    /**
     * Request a secure tunnel to a host for a specific remote port
     * @param host Hostname
     * @param port Remote service port
     * @return TunnelRequest instance exposing the local port to connect to
     */
    @Synchronized fun request(
            host: String,
            port: Int): TunnelRequest {

        // Lookup tunnel spec
        val tunnelSpec = this.tunnelConfigurations.get(host)
                ?: throw IllegalArgumentException("No ssh tunnel spec for host [${host}}")

        val key = TunnelKey(Thread.currentThread().id, host, port)

        val tunnelRecord = this.tunnelRecords
                .getOrPut(key, {
                    // Get free local port from range
                    val localPort = this.localPorts.pop()

                    // Create new SSH tunnel for connection request
                    TunnelRecord(
                            SshTunnel(
                                    host = tunnelSpec.host,
                                    sshPort = tunnelSpec.sshPort,
                                    remotePort = port,
                                    localPort = localPort,
                                    sshUsername = tunnelSpec.sshUsername,
                                    sshPassword = tunnelSpec.sshPassword))
                })

        tunnelRecord.sshTunnel.open()
        tunnelRecord.count++

        return TunnelRequest(key, tunnelRecord.sshTunnel.localPort)
    }

    @Synchronized fun release(request: TunnelRequest) {
        val tunnelRecord = this.tunnelRecords.get(request.key)
                ?: throw IllegalArgumentException("Unknwon tunnel request [${request}]")

        tunnelRecord.count--
        if (tunnelRecord.count <= 0) {
            tunnelRecord.sshTunnel.close()
            this.tunnelRecords.remove(request.key)
            this.localPorts.push(request.localPort)
        }
    }
}