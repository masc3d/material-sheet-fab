package sx.jms.embedded.activemq

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.RedeliveryPolicy
import org.apache.activemq.broker.BrokerPlugin
import org.apache.activemq.broker.BrokerPluginSupport
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap
import org.apache.activemq.broker.util.RedeliveryPlugin
import org.apache.activemq.command.ActiveMQQueue
import org.apache.activemq.command.ActiveMQTopic
import org.apache.activemq.command.BrokerInfo
import org.apache.activemq.filter.DestinationMapEntry
import org.apache.activemq.jms.pool.PooledConnectionFactory
import org.apache.activemq.leveldb.LevelDBStore
import org.apache.activemq.leveldb.LevelDBStoreFactory
import org.apache.activemq.network.DiscoveryNetworkConnector
import org.apache.activemq.security.*
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter
import org.apache.activemq.transport.TransportServer
import sx.jms.embedded.Broker
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.jms.ConnectionFactory
import javax.jms.IllegalStateException
import javax.jms.Queue
import javax.jms.Topic

/**
 * Broker implementation for activemq
 * Created by masc on 16.04.15.
 */
class ActiveMQBroker private constructor()
:
        Broker(ActiveMQBroker.NATIVE_TCP_PORT) {
    /**
     * Persistence store type
     */
    enum class StoreType(val storeType: String) {
        KahaDb("kahadb"),
        LevelDb("leveldb");

        override fun toString(): String {
            return storeType
        }
    }

    /** Native broker service  */
    @Volatile private var brokerService: BrokerService? = null

    /** Persistence store type to use  */
    private val storeType = StoreType.KahaDb

    /** External transport servers, eg. servlets  */
    internal var externalTransportServers: MutableList<TransportServer> = ArrayList()

    /** Url for establishing connection to local/embedded broker  */
    private val localUri: URI
        get() = URI("vm://${brokerName}?create=false")

    /**
     * Add transport server, eg. from servlet
     */
    fun addConnector(transportServer: TransportServer) {
        externalTransportServers.add(transportServer)
    }

    /**
     * Start broker
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun startImpl() {
        this.stop()

        if (this.user == null)
            throw IllegalStateException("Broker user not set")

        // Broker initialization
        brokerService = BrokerService()

        // Basic options (order is very relevant, eg. not setting broker name will result in borker data directory for persistence adapters)
        brokerService!!.brokerName = this.brokerName
        // Required for redelivery plugin/policy
        brokerService!!.isSchedulerSupport = true
        // Disabling activemq's integrated  shutdown hook, favoring consumer side ordered shutdown
        brokerService!!.isUseShutdownHook = false

        // Persistence setup
        val persistenceStoreDirectory = File(this.dataDirectory, storeType.toString())
        brokerService!!.dataDirectoryFile = persistenceStoreDirectory

        when (storeType) {
            ActiveMQBroker.StoreType.KahaDb -> {
                val pa = brokerService!!.persistenceAdapter as KahaDBPersistenceAdapter
                pa.isCheckForCorruptJournalFiles = true
            }
            ActiveMQBroker.StoreType.LevelDb -> {
                brokerService!!.persistenceFactory = LevelDBStoreFactory()
                val pa = brokerService!!.persistenceAdapter as LevelDBStore
                pa.directory = persistenceStoreDirectory
            }
            else -> throw IllegalStateException(String.format("Unknown store type [%s]", storeType))
        }

        // Create VM broker for direct (in memory/vm) connections.
        // The Broker name has to match for clients to connect
        brokerService!!.addConnector("vm://${this.brokerName}")

        // Statically defined transport connectors for native clients to connect to
        brokerService!!.addConnector(String.format("tcp://0.0.0.0:%d",
                this.nativeTcpPort))

        // Peer/network connectors for brokers to inter-connect
        for (pb in this.peerBrokers) {
            val hostUrl = createUri(pb, false)
            val nc = DiscoveryNetworkConnector(URI.create(String.format("static:(%s)", hostUrl)))
            nc.userName = this.user!!.userName
            nc.password = this.user!!.password
            nc.isDuplex = true
            brokerService!!.addNetworkConnector(nc)
        }
        for (ts in externalTransportServers) {
            brokerService!!.addConnector(ts)
        }

        // Broker plugins
        val brokerPlugins = ArrayList<BrokerPlugin>()

        //region Authentication
        val pAuth = SimpleAuthenticationPlugin()

        // Users
        val users = ArrayList<AuthenticationUser>()
        users.add(AuthenticationUser(this.user!!.userName, this.user!!.password, this.user!!.groupName))
        pAuth.setUsers(users)

        brokerPlugins.add(pAuth)
        //endregion

        //region Authorizations
        val authzEntries = ArrayList<AuthorizationEntry>()

        val group = this.user!!.groupName

        var pAuthzEntry: AuthorizationEntry
        // Leo group, all access
        pAuthzEntry = AuthorizationEntry()
        pAuthzEntry.setTopic(">")
        pAuthzEntry.setAdmin(group)
        pAuthzEntry.setRead(group)
        pAuthzEntry.setWrite(group)
        authzEntries.add(pAuthzEntry)

        pAuthzEntry = AuthorizationEntry()
        pAuthzEntry.setQueue(">")
        pAuthzEntry.setAdmin(group)
        pAuthzEntry.setRead(group)
        pAuthzEntry.setWrite(group)
        authzEntries.add(pAuthzEntry)

        // Leo group, all access to temp destinations
        pAuthzEntry = TempDestinationAuthorizationEntry()
        pAuthzEntry.setTopic(">")
        pAuthzEntry.setAdmin(group)
        pAuthzEntry.setRead(group)
        pAuthzEntry.setWrite(group)
        authzEntries.add(pAuthzEntry)

        pAuthzEntry = TempDestinationAuthorizationEntry()
        pAuthzEntry.setQueue(">")
        pAuthzEntry.setAdmin(group)
        pAuthzEntry.setRead(group)
        pAuthzEntry.setWrite(group)
        authzEntries.add(pAuthzEntry)

        // Create authorization map from entries
        val authzMap = DefaultAuthorizationMap()
        authzMap.setAuthorizationEntries(authzEntries as List<DestinationMapEntry<Any>>?)

        // Authorization plugin
        val pAuthz = AuthorizationPlugin()
        pAuthz.map = authzMap

        brokerPlugins.add(pAuthz)
        //endregion

        //region Redelivery policy
        val pRedelivery = RedeliveryPlugin()
        // TODO: verify if those plugin options are really needed
        pRedelivery.isFallbackToDeadLetter = true
        pRedelivery.isSendToDlqIfMaxRetriesExceeded = true

        val rpm = RedeliveryPolicyMap()

        // TODO: define sensible values for redelivery of messages
        val rp = RedeliveryPolicy()
        rp.maximumRedeliveries = 3
        rp.initialRedeliveryDelay = 2000
        rp.backOffMultiplier = 2.0
        rp.isUseExponentialBackOff = true

        rpm.defaultEntry = rp
        pRedelivery.redeliveryPolicyMap = rpm
        //endregion

        brokerPlugins.add(pRedelivery)

        // Network/bridge connection notification plugin
        brokerPlugins.add(object : BrokerPluginSupport() {
            override fun networkBridgeStarted(brokerInfo: BrokerInfo, createdByDuplex: Boolean, remoteIp: String) {
                super.networkBridgeStarted(brokerInfo, createdByDuplex, remoteIp)
                listenerEventDispatcher.emit { e -> e.onConnectedToBrokerNetwork() }
            }

            override fun networkBridgeStopped(brokerInfo: BrokerInfo) {
                super.networkBridgeStopped(brokerInfo)
                listenerEventDispatcher.emit { e -> e.onDisconnectedFromBrokerNetwork() }
            }
        })

        // Add all plugins to broker service
        brokerService!!.plugins = brokerPlugins.toArray(arrayOfNulls<BrokerPlugin>(0))

        try {
            brokerService!!.start()
        } catch (e: Exception) {
            try {
                brokerService!!.stop()
            } catch (e2: Exception) {
                log.error(e2.message, e2)
            }

            throw e
        }
    }

    /**
     * Stop broker
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun stopImpl() {
        val brokerService = this.brokerService
        if (brokerService != null && brokerService.isStarted) {
            brokerService.stop()
            this.brokerService = null
        }
    }

    override val isStartedImpl: Boolean
        get() {
            val brokerService = brokerService
            return brokerService != null && brokerService.isStarted
        }

    override fun createQueue(name: String): Queue {
        return ActiveMQQueue(name)
    }

    override fun createTopic(name: String): Topic {
        return ActiveMQTopic(name)
    }

    override val connectionFactory: ConnectionFactory by lazy({
        val psf = PooledConnectionFactory()
        val cf = ActiveMQConnectionFactory(
                this.user!!.userName,
                this.user!!.password,
                localUri.toString())
        cf.isWatchTopicAdvisories = false
        psf.connectionFactory = cf
        psf
    })

    companion object {
        // Defaults
        private val NATIVE_TCP_PORT = 61616

        /** Singleton */
        @JvmStatic val instance by lazy({ ActiveMQBroker() })

        /**
         * Create ActiveMQ URI
         * @param peerBroker Peer broker
         * @param failover
         * @return ActiveMQ URI
         */
        fun createUri(peerBroker: Broker.PeerBroker, failover: Boolean): URI {
            var scheme = peerBroker.transportType.toString()
            var path = peerBroker.httpPath ?: ""
            var port = peerBroker.port ?: if (peerBroker.transportType === Broker.TransportType.TCP) 61616 else 80

            if (failover)
                scheme = "failover:" + scheme

            try {
                return URI(String.format("%s://%s:%d%s",
                        scheme,
                        peerBroker.hostname,
                        port,
                        path))
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            }
        }
    }
}
