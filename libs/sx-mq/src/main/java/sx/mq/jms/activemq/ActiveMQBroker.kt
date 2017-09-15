package sx.mq.jms.activemq

import org.apache.activemq.RedeliveryPolicy
import org.apache.activemq.broker.BrokerPlugin
import org.apache.activemq.broker.BrokerPluginSupport
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.broker.region.DestinationInterceptor
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap
import org.apache.activemq.broker.region.virtual.CompositeDestination
import org.apache.activemq.broker.region.virtual.MirroredQueue
import org.apache.activemq.broker.region.virtual.VirtualDestination
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor
import org.apache.activemq.broker.region.virtual.VirtualTopic
import org.apache.activemq.broker.util.RedeliveryPlugin
import org.apache.activemq.command.BrokerInfo
import org.apache.activemq.filter.DestinationMapEntry
import org.apache.activemq.network.DiscoveryNetworkConnector
import org.apache.activemq.security.AuthenticationUser
import org.apache.activemq.security.AuthorizationEntry
import org.apache.activemq.security.AuthorizationPlugin
import org.apache.activemq.security.DefaultAuthorizationMap
import org.apache.activemq.security.SimpleAuthenticationPlugin
import org.apache.activemq.security.TempDestinationAuthorizationEntry
import org.apache.activemq.store.PersistenceAdapter
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter
import org.apache.activemq.store.kahadb.disk.journal.Journal
import org.apache.activemq.transport.TransportServer
import org.threeten.bp.Duration
import sx.mq.MqBroker
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.jms.IllegalStateException

/**
 * Broker implementation for activemq
 * Created by masc on 16.04.15.
 */
class ActiveMQBroker private constructor()
    :
        MqBroker(NATIVE_TCP_PORT) {

    companion object {
        // Defaults
        private val NATIVE_TCP_PORT = 61616

        /** Singleton */
        @JvmStatic
        val instance by lazy({ ActiveMQBroker() })

        /**
         * Create ActiveMQ URI
         * @param peerBroker Peer broker
         * @param failover
         * @return ActiveMQ URI
         */
        private fun createUri(peerBroker: PeerBroker, failover: Boolean): URI {
            var scheme = peerBroker.transportType.toString()
            val path = peerBroker.httpPath ?: ""
            val port = peerBroker.port ?: if (peerBroker.transportType === TransportType.TCP) 61616 else 80

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

    /** Native broker service  */
    @Volatile private var brokerService: BrokerService? = null

    /** Composite destinations to statically add on startup */
    private val compositeDestinations = ArrayList<CompositeDestination>()

    /** External transport servers, eg. servlets  */
    internal var externalTransportServers: MutableList<TransportServer> = ArrayList()

    /** Url for establishing connection to local/embedded broker  */
    val localUri: URI
        get() = URI("vm://${brokerName}?create=false")

    /**
     * Add transport server, eg. from servlet
     */
    fun addConnector(transportServer: TransportServer) {
        externalTransportServers.add(transportServer)
    }

    /**
     * Add composite destination
     */
    fun addCompositeDestination(destination: CompositeDestination) {
        this.compositeDestinations.add(destination)
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
        val brokerService = BrokerService()

        // Basic options (order is very relevant, eg. not setting broker name will result in borker data directory for persistence adapters)
        brokerService.brokerName = this.brokerName
        // Required for redelivery plugin/policy
        brokerService.isSchedulerSupport = true
        // Disabling activemq's integrated  shutdown hook, favoring consumer side ordered shutdown
        brokerService.isUseShutdownHook = false
        // Allow temporary queue auto creation for producers.
        // Fixes sporadic issues with request/response where a remote broker's producer responds via temporary queue before it had the opportunity to create it (per advisory message)
        // For reference: https://issues.apache.org/jira/browse/AMQ-3253
        brokerService.isAllowTempAutoCreationOnSend = true

        //region Persistence
        val persistenceStoreDirectory = File(this.dataDirectory, "kahadb")
        brokerService.dataDirectoryFile = persistenceStoreDirectory

        val pa: PersistenceAdapter
        pa = brokerService.persistenceAdapter as KahaDBPersistenceAdapter
        pa.isCheckForCorruptJournalFiles = true
        pa.preallocationScope = Journal.PreallocationScope.ENTIRE_JOURNAL_ASYNC.name
        pa.preallocationStrategy = Journal.PreallocationStrategy.ZEROS.name

        // Enforce our own persistence store directory for both regular store and scheduler, overriding the default which has broker name in its path
        pa.directory = persistenceStoreDirectory
        brokerService.setSchedulerDirectory(persistenceStoreDirectory.resolve("scheduler").toString())
        //endregion

        // Create VM broker for direct (in memory/vm) connections.
        // The Broker name has to match for clients to connect
        brokerService.addConnector("vm://${this.brokerName}")

        // Statically defined transport connectors for native clients to connect to
        brokerService.addConnector("auto+nio://0.0.0.0:${this.nativeTcpPort}")

        // Peer/network connectors for brokers to inter-connect
        for (pb in this.peerBrokers) {
            val hostUrl = createUri(pb, false)
            val nc = DiscoveryNetworkConnector(URI.create(String.format("static:(%s)", hostUrl)))
            nc.userName = this.user!!.userName
            nc.password = this.user!!.password
            nc.isDuplex = true

            brokerService.addNetworkConnector(nc)
        }
        for (ts in externalTransportServers) {
            brokerService.addConnector(ts)
        }

        // Broker plugins
        val brokerPlugins = ArrayList<BrokerPlugin>()

        fun createAuthenticationPlugin(): BrokerPlugin {
            val pAuth = SimpleAuthenticationPlugin()

            // Users
            val users = ArrayList<AuthenticationUser>()
            users.add(AuthenticationUser(this.user!!.userName, this.user!!.password, this.user!!.groupName))
            pAuth.setUsers(users)

            return pAuth
        }

        fun createAuthorizationPlugin(): BrokerPlugin {
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

            return pAuthz
        }

        fun createRedeliveryPlugin(): BrokerPlugin {
            val pRedelivery = RedeliveryPlugin()
            // TODO: verify if those plugin options are really needed
            pRedelivery.isFallbackToDeadLetter = true
            pRedelivery.isSendToDlqIfMaxRetriesExceeded = true

            val rpm = RedeliveryPolicyMap()

            // TODO: define sensible values for redelivery of messages
            val rp = RedeliveryPolicy()
            rp.isUseExponentialBackOff = true
            rp.initialRedeliveryDelay = Duration.ofSeconds(5).toMillis()
            rp.backOffMultiplier = 2.0
            rp.maximumRedeliveryDelay = Duration.ofMinutes(15).toMillis()

            val maxRedeliveryTime = Duration.ofDays(1).toMillis()

            // Calculate maximum redeliveries from above parameters

            val maxRedeliveries = (maxRedeliveryTime / rp.maximumRedeliveryDelay) +
                    // Add approximate retries for exponential backoff
                    (Math.log(rp.maximumRedeliveryDelay.toDouble() / rp.initialRedeliveryDelay) / Math.log(rp.backOffMultiplier))

            rp.maximumRedeliveries = Math.ceil(maxRedeliveries).toInt()

            rpm.defaultEntry = rp
            pRedelivery.redeliveryPolicyMap = rpm
            //endregion

            return pRedelivery
        }

        fun createNotificationPlugin(): BrokerPlugin {
            return object : BrokerPluginSupport() {

                private val peerBrokersByName = ConcurrentHashMap<String, PeerBroker>()

                /**
                 * Convert remote ip string from ActiveMQ to valid URI
                 * @param remoteIp ActiveMQ remote ip string
                 * @return URI
                 */
                private fun uriFromRemoteIp(remoteIp: String): URI {
                    return URI.create(remoteIp.replace("///", "//").substringBeforeLast("@"))
                }

                override fun networkBridgeStarted(brokerInfo: BrokerInfo, createdByDuplex: Boolean, remoteIp: String) {
                    super.networkBridgeStarted(brokerInfo, createdByDuplex, remoteIp)

                    try {
                        val remoteUri = this.uriFromRemoteIp(remoteIp)
                        // Find peer broker matching hostname
                        val peerBroker = this@ActiveMQBroker.peerBrokers.firstOrNull {
                            it.hostname == remoteUri.host
                        }
                        if (peerBroker != null) {
                            this.peerBrokersByName.put(brokerInfo.brokerName, peerBroker)

                            this.brokerService.taskRunnerFactory.execute {
                                listenerEventDispatcher.emit { e -> e.onConnectedToBrokerNetwork() }
                            }
                        }
                    } catch (e: Exception) {
                        log.info(remoteIp)
                        log.error(e.message, e)
                    }
                }

                override fun networkBridgeStopped(brokerInfo: BrokerInfo) {
                    super.networkBridgeStopped(brokerInfo)

                    val peerBroker = this.peerBrokersByName.remove(brokerInfo.brokerName)

                    if (peerBroker != null) {
                        this.brokerService.taskRunnerFactory.execute {
                            listenerEventDispatcher.emit { e -> e.onDisconnectedFromBrokerNetwork() }
                        }
                    }
                }
            }
        }

        brokerPlugins.addAll(listOf(
                createAuthenticationPlugin(),
                createAuthorizationPlugin(),
                createRedeliveryPlugin(),
                createNotificationPlugin()))

        // Add all plugins to broker service
        brokerService.plugins = brokerPlugins.toTypedArray()

        fun createDestinationInterceptors(): List<DestinationInterceptor> {
            /**
             * This method is a replica of the protected {@link BrokerService.createDefaultDestinationIntercept}
             * @param compositeDestinations Additional composite destinations
             */
            fun createDefaultDestinationInterceptors(compositeDestinations: List<CompositeDestination> = listOf()): List<DestinationInterceptor> {
                val answer = ArrayList<DestinationInterceptor>()
                if (brokerService.isUseVirtualTopics) {
                    val interceptor = VirtualDestinationInterceptor()
                    val virtualTopic = VirtualTopic()
                    virtualTopic.name = "VirtualTopic.>"
                    val virtualDestinations = mutableListOf<VirtualDestination>(virtualTopic)
                    // Add custom composite destinations
                    virtualDestinations.addAll(compositeDestinations)
                    interceptor.virtualDestinations = virtualDestinations.toTypedArray()
                    answer.add(interceptor)
                }
                if (brokerService.isUseMirroredQueues) {
                    val interceptor = MirroredQueue()
                    answer.add(interceptor)
                }
                return answer
            }

            return createDefaultDestinationInterceptors(this.compositeDestinations)
        }

        brokerService.destinationInterceptors = createDestinationInterceptors().toTypedArray()

        try {
            brokerService.start()
        } catch (e: Exception) {
            try {
                brokerService.stop()
            } catch (e2: Exception) {
                log.error(e2.message, e2)
            }

            throw e
        }

        this.brokerService = brokerService
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
}
