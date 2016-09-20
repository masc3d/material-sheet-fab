package org.deku.leoz.node

import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.peer.RemotePeerSettings
import sx.Lifecycle
import sx.jms.Broker
import java.lang.ref.WeakReference
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named

/**
 * Controls various aspect of lifecycles, for example network dependent lifecycle which require
 * (re)start when the connection is lost temporarily.
 * Created by masc on 24/03/16.
 */
@Named
class LifecycleController {

    val lifecycles = Collections.synchronizedList(ArrayList<WeakReference<Lifecycle>>())

    @Inject
    private lateinit var peerSettings: RemotePeerSettings

    /**
     * Indiciates if a remote connection is required for network dependent lifecycles.
     * An example where remote connectivity is not required is leoz-central, which basically communicates to itself
     * if it needs to.
     */
    private val requiresRemoteConnection by lazy { !peerSettings.hostname.isNullOrEmpty() }

    /**
     * Broker listener
     */
    val brokerListener = object : Broker.EventListener {
        override fun onStart() {
            // If remote connection is not required, lifecycles start when the broker starts
            if (!this@LifecycleController.requiresRemoteConnection) {
                this@LifecycleController.lifecycles.iterator().forEach {
                    it.get()?.restart()
                }
            }
        }

        override fun onStop() {
            // All lifecycles end with the broker dying
            this@LifecycleController.lifecycles.iterator().forEach {
                it.get()?.stop()
            }
        }

        override fun onConnectedToBrokerNetwork() {
            // Network dependent lifecycles need to restart if this application instance relies on remote connectivity
            if (this@LifecycleController.requiresRemoteConnection) {
                this@LifecycleController.lifecycles.iterator().forEach {
                    it.get()?.restart()
                }
            }
        }

        override fun onDisconnectedFromBrokerNetwork() {
            //  Network dependent lifecycles need to stop if this application instance relies on remote connectivity
            if (this@LifecycleController.requiresRemoteConnection) {
                this@LifecycleController.lifecycles.iterator().forEach {
                    it.get()?.stop()
                }
            }
        }
    }

    /**
     * Register a connection dependent lifecycle, which will be (re)started when the connection to the
     * node network is established and stopped when the connection is lost.
     * @param lifecycle Lifecycle to register
     */
    fun registerNetworkDependant(lifecycle: Lifecycle) {
        this.lifecycles.add(WeakReference(lifecycle))
    }

    @PostConstruct
    fun onInitialize() {
        ActiveMQConfiguration.instance.broker.delegate.add(this.brokerListener)
    }
}