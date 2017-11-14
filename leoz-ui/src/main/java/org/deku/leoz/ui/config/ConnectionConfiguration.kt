package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.internal.DiscoveryInfo
import org.deku.leoz.service.internal.DiscoveryService
import org.slf4j.LoggerFactory
import io.reactivex.subjects.PublishSubject
import sx.net.UdpDiscoveryService
import kotlin.properties.Delegates

/**
 * Connection configuration. Unifies discovery and connection configuration of micro services
 * Created by masc on 24/11/2016.
 */
class ConnectionConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val module = Kodein.Module {
            bind<ConnectionConfiguration>() with eagerSingleton {
                val config = ConnectionConfiguration()

                val discoveryService: DiscoveryService = instance()
                discoveryService.updatedEvent.subscribe {
                    config.node = discoveryService.directory.firstOrNull {
                        it.info?.bundleType == BundleType.LEOZ_NODE
                    }
                }
                config
            }
        }
    }

    // Injections
    private val restClientFactory: RestClientFactory by Kodein.global.lazy.instance()
    private val bundleConfiguration: BundleConfiguration by Kodein.global.lazy.instance()

    class NodeUpdatedEvent(
            val node: UdpDiscoveryService.Node<DiscoveryInfo>? = null
    )

    // Events
    private val _nodeUpdatedEvent = PublishSubject.create<NodeUpdatedEvent>()
    /** Node update event */
    val nodeUpdatedEvent by lazy { _nodeUpdatedEvent.hide() }

    /**
     * Active node that services should connect to
     */
    var node: UdpDiscoveryService.Node<DiscoveryInfo>? by Delegates.observable(null) {
        _,
        o: UdpDiscoveryService.Node<DiscoveryInfo>?,
        n: UdpDiscoveryService.Node<DiscoveryInfo>? ->

        val DEFAULT_HOST = "localhost"
        val oldHost = o?.address?.hostName ?: DEFAULT_HOST
        val newHost = n?.address?.hostName ?: DEFAULT_HOST

        if (oldHost != newHost) {
            // Delegate setting to configurations
            this.restClientFactory.host = newHost
            this.bundleConfiguration.rsyncHost = newHost
            log.info("Updated remote host to [${newHost}]")

            _nodeUpdatedEvent.onNext(NodeUpdatedEvent(n))
        }
    }
}
