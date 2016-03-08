package sx.ssh

import org.apache.commons.logging.LogFactory
import java.net.BindException
import java.time.Duration
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

    /** Synchronization object */
    private val sync = Object()
    /** Local port pool */
    private val localPorts = LinkedList<Int>()
    /** SSH tunnel configuration by hostname */
    private val sshHosts = HashMap<String, SshHost>()
    /** SSH tunnel maps by composite tunnel key */
    private val tunnels = HashMap<TunnelKey, SshTunnel>()
    /** Idle timeout for ssh tunnels */
    var idleTimeout: Duration = Duration.ofSeconds(30)

    /**
     * SSH tunnel composite key for record/tunnel lookups.
     */
    data class TunnelKey(
            val threadId: Long,
            val host: String,
            val port: Int) {
    }

    /**
     * SSH tunnel resource, handed out to the requestor for providing
     * connection information (local port).
     */
    inner class TunnelResource(
            val key: TunnelKey,
            val localPort: Int) {
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
     * @return TunnelRequest instance exposing the local port to connect to or null if provider doesn't have host information
     */
    fun request(
            hostname: String,
            port: Int): TunnelResource? {

        synchronized(sync) {
            // Lookup tunnel spec
            val sshHost = this.sshHosts.get(hostname) ?: return null

            val key = TunnelKey(Thread.currentThread().id, hostname, port)

            var tunnelResource: TunnelResource? = null
            do {
                // Get existing tunnel for host or create new one
                val tunnel = this.tunnels
                        .getOrPut(key, {
                            if (this.localPorts.count() == 0)
                                throw IllegalStateException("Local port range exhausted")

                            // Get free local port from range
                            val localPort = this.localPorts.pop()

                            tunnelResource = TunnelResource(key, localPort)

                            // Create new SSH tunnel for connection request
                            SshTunnel(
                                    sshHost = sshHost,
                                    remotePort = port,
                                    localPort = localPort,
                                    idleTimeout = this.idleTimeout,
                                    // Free tunnel resource/local port when connection is closed
                                    onClosed = { it ->
                                        this.release(tunnelResource!!)
                                    }
                            )
                        })

                // Create new tunnel resource for existing tunnel
                if (tunnelResource == null)
                    tunnelResource = TunnelResource(key, tunnel.localPort)

                try {
                    tunnel.open()
                } catch(e: Throwable) {
                    tunnel.close()
                    this.release(tunnelResource!!)

                    if (e is BindException) {
                        // Local port in use, remove from port range and retry
                        log.warn("Local port [${tunnelResource!!.localPort}] in use externally. Removing from port range.")
                        this.localPorts.removeFirstOccurrence(tunnelResource!!.localPort)
                        tunnelResource = null
                    } else {
                        // Anything else is fatal
                        throw e
                    }
                }
            } while (tunnelResource == null)

            return tunnelResource!!
        }
    }

    /**
     * Explicitly close tunnel collection using requested tunnel resource
     * @param resource Tunnel resource representing a tunnel (returned by {@link request})
     */
    fun close(resource: TunnelResource) {
        var tunnel: SshTunnel? = null

        synchronized(sync) {
            tunnel = this.tunnels.get(resource.key)
        }

        if (tunnel != null)
            tunnel!!.close()
    }

    /**
     * Release tunnel, removes reference and pushes local port back to pool
     * @param resource Tunnel resource
     */
    private fun release(resource: TunnelResource) {
        synchronized(sync) {
            if (!this.tunnels.contains(resource.key)) {
                log.warn("Cannot release unknown tunnel resource [${resource}]")
                return
            }

            this.close(resource);

            // Remove tunnel record and push local port back into pool
            this.tunnels.remove(resource.key)
            this.localPorts.push(resource.localPort)
        }
    }
}