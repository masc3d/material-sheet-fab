package sx.ssh

import org.apache.commons.logging.LogFactory
import java.util.*

/**
 * SSH tunnel provider.
 * Managing and providing SSH tunnels with multithreading support.
 * Created by masc on 25.11.15.
 * @property localPortRange Local port range to use
 * @property sshHosts Array of SSH hosts
 */
class SshTunnelProvider(
        val localPortRange: IntRange,
        vararg sshHosts: SshHost
) {
    private val log = LogFactory.getLog(this.javaClass)

    private val localPorts = LinkedList<Int>()
    /** SSH tunnel configuration by hostname */
    private val sshHosts = HashMap<String, SshHost>()
    /** SSH tunnel maps by composite tunnel key */
    private val tunnelRecords = HashMap<TunnelKey, TunnelRecord>()

    /**
     * SSH tunnel composite key for record/tunnel lookups.
     */
    data class TunnelKey(
            val threadId: Long,
            val host: String,
            val port: Int) {
    }

    /**
     * SSH tunnel record
     * @property sshTunnel SSH tunnel
     */
    private class TunnelRecord(
            val sshTunnel: SshTunnel) {

        /** Tunnel reference count */
        var refCount: Int = 0
    }

    /**
     * SSH tunnel resource, handed out to the requestor for providing
     * connection information (local port). Should be released using
     * {@link #release}.
     * Automatically triggers release/closes tunnel when finalized/gc'ed
     */
    inner class TunnelResource(
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

    /** c'tor */
    init {
        this.localPorts.addAll(this.localPortRange)
        sshHosts.forEach { t -> this.sshHosts.put(t.hostname, t) }
    }

    /**
     * Request a secure tunnel to a host for a specific remote port
     * @param hostname Hostname
     * @param port Remote service port
     * @return TunnelRequest instance exposing the local port to connect to
     * @throws IllegalArgumentException If provider does not have ssh connection information about the requested host
     */
    @Synchronized fun request(
            hostname: String,
            port: Int): TunnelResource {

        // Lookup tunnel spec
        val sshHost = this.sshHosts.get(hostname)
                ?: throw IllegalArgumentException("No ssh connection info for [${hostname}}")

        val key = TunnelKey(Thread.currentThread().id, hostname, port)

        val tunnelRecord = this.tunnelRecords
                .getOrPut(key, {
                    // Get free local port from range
                    val localPort = this.localPorts.pop()

                    // Create new SSH tunnel for connection request
                    TunnelRecord(
                            SshTunnel(
                                    host = sshHost,
                                    remotePort = port,
                                    localPort = localPort
                            )
                    )
                })

        tunnelRecord.sshTunnel.open()
        tunnelRecord.refCount++

        return TunnelResource(key, tunnelRecord.sshTunnel.localPort)
    }

    @Synchronized fun release(resource: TunnelResource) {
        val tunnelRecord = this.tunnelRecords.get(resource.key)
                ?: throw IllegalArgumentException("Unknown tunnel resource [${resource}]")

        tunnelRecord.refCount--
        if (tunnelRecord.refCount <= 0) {
            tunnelRecord.sshTunnel.close()
            // Remove tunnel record and push local port back into pool
            this.tunnelRecords.remove(resource.key)
            this.localPorts.push(resource.localPort)
        }
    }
}