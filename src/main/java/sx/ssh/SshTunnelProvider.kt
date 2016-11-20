package sx.ssh

import org.slf4j.LoggerFactory
import java.io.Closeable
import java.net.BindException
import java.time.Duration
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * SSH tunnel provider.
 * Manages, provides and pools SSH tunnels. This class is threadsafe.
 * Created by masc on 25.11.15.
 * @property localPortRange Local port range to use
 * @property sshHosts Array of SSH hosts. Default credentials may be provided with an `SshHost` having empty string as hostname
 */
class SshTunnelProvider(
        val localPortRange: IntRange,
        vararg sshHosts: SshHost
) : AutoCloseable {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Synchronization object */
    private val lock = ReentrantLock()
    /** SSH tunnel configuration by hostname */
    private val sshHosts = HashMap<String, SshHost>()
    /** Local port pool */
    private val localPortPool = LinkedList<Int>()
    /** Map of allocated/ppen SSH tunnels by key and thread id */
    private val tunnelPools = HashMap<TunnelKey, TunnelPool>()
    /** Idle timeout for ssh tunnels */
    var idleTimeout: Duration = Duration.ofSeconds(30)

    /**
     * Tunnel pool
     */
    private class TunnelPool {
        val allocated = HashMap<Long, SshTunnel>()
        val released = LinkedList<SshTunnel>()
    }

    /**
     * SSH tunnel composite key for record/tunnel lookups.
     * @property host Remote host
     * @property port Remote port
     */
    data class TunnelKey(
            val host: String,
            val port: Int) {
    }

    /**
     * SSH tunnel resource, handed out to the consumer
     * @property key Tunnel key
     * @property localPort The local tunnel port for this connection. Merely for the consumer to know which port to (locally) connect to
     */
    inner class TunnelResource(
            val key: TunnelKey,
            val localPort: Int) : Closeable {
        override fun close() {
            release(this.key)
        }
    }

    /** c'tor */
    init {
        this.localPortPool.addAll(this.localPortRange)
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

        var tunnelResource: TunnelResource? = null

        lock.withLock {
            // Lookup tunnel spec
            val sshHost = this.sshHosts.getOrPut(hostname,
                    {
                        val defaultSshHost = this.sshHosts.get("") ?: return@withLock null

                        SshHost(
                                hostname = hostname,
                                port = defaultSshHost.port,
                                username = defaultSshHost.username,
                                password = defaultSshHost.password)
                    })

            val key = TunnelKey(hostname, port)

            do {
                // Get existing tunnel for host or create new one
                val pool = this.tunnelPools.getOrPut(key, {
                    TunnelPool()
                })

                val tunnel = pool.allocated.getOrPut(Thread.currentThread().id, {
                    if (pool.released.size > 0) {
                        log.trace("Reusing tunnel from pool")
                        pool.released.pop()
                    } else {
                        if (this.localPortPool.count() == 0)
                            throw IllegalStateException("Local port range exhausted")

                        // Get free local port from range
                        val localPort = this.localPortPool.pop()

                        tunnelResource = TunnelResource(key, localPort)

                        log.trace("Creating new tunnel")
                        // Create new SSH tunnel for connection request
                        SshTunnel(
                                sshHost = sshHost,
                                remotePort = port,
                                localPort = localPort,
                                idleTimeout = this.idleTimeout,
                                // Free tunnel resource/local port when connection is closed
                                onClosed = { me ->
                                    this.purge(me)
                                }
                        )
                    }
                })

                // Create new tunnel resource for existing tunnel
                if (tunnelResource == null)
                    tunnelResource = TunnelResource(key, tunnel.localPort)

                try {
                    tunnel.open()
                } catch(e: Throwable) {
                    tunnel.close()
                    release(tunnelResource!!.key)

                    if (e is BindException) {
                        // Local port in use, remove from port range and retry
                        log.warn("Local port [${tunnelResource!!.localPort}] in use externally. Removing from port range.")
                        this.localPortPool.removeFirstOccurrence(tunnelResource!!.localPort)
                        tunnelResource = null
                    } else {
                        // Anything else is fatal
                        throw e
                    }
                }
            } while (tunnelResource == null)
        }

        return tunnelResource
    }

    private fun poolForKey(key: TunnelKey): TunnelPool {
        return this.tunnelPools[key] ?: throw IllegalArgumentException("Cannot determine pool for [${key}]")
    }

    /**
     * Purge tunnel
     */
    private fun purge(tunnel: SshTunnel) {
        lock.withLock {
            tunnel.close()

            val key = TunnelKey(host = tunnel.sshHost.hostname, port = tunnel.remotePort)
            val tunnelPool = this.poolForKey(key)

            val releasedIndex = tunnelPool.released.indexOf(tunnel)
            if (releasedIndex >= 0) {
                log.trace("Removing released tunnel [${tunnel}]")
                // Remove from released list
                tunnelPool.released.removeAt(releasedIndex)
            } else {
                log.trace("Tunnel unreleased, attempting to remove tunnel from allocated pool [${tunnel}]")
                val threadId = tunnelPool.allocated.filter {
                    it.value == tunnel
                }
                        .keys
                        .firstOrNull() ?: throw IllegalArgumentException("Unknown tunnel [${tunnel}]")

                tunnelPool.allocated.remove(threadId)
            }

            this.localPortPool.push(tunnel.localPort)
        }
    }

    /**
     * Release tunnel, removes reference and pushes local port back to pool
     * @param resource Tunnel resource
     */
    private fun release(key: TunnelKey) {
        log.trace("Releasing tunnel with key [${key}]")

        lock.withLock {
            val tunnelPool = this.poolForKey(key)
            val tunnel = tunnelPool.allocated[Thread.currentThread().id]

            if (tunnel != null) {
                // Remove tunnel record and push local port back into pool
                tunnelPool.allocated.remove(Thread.currentThread().id)
                tunnelPool.released.push(tunnel)
            }
        }
    }

    override fun close() {
        lock.withLock {
            this.tunnelPools.flatMap {
                val list = arrayListOf(*it.value.allocated.values.toTypedArray())
                list.addAll(it.value.released)
                list
            }.forEach {
                try {
                    log.info("Closing tunnel ${it}")
                    it.close()
                } catch(e: Throwable) {
                    log.error(e.message, e)
                }
            }
        }
    }
}