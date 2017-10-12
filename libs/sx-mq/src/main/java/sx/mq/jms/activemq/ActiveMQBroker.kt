package sx.mq.jms.activemq

import org.apache.activemq.RedeliveryPolicy
import org.apache.activemq.broker.BrokerPlugin
import org.apache.activemq.broker.BrokerPluginSupport
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.broker.region.DestinationInterceptor
import org.apache.activemq.broker.region.policy.PolicyEntry
import org.apache.activemq.broker.region.policy.PolicyMap
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy
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
     * Redelivery duration before messages are sent to DLQ (dead letter queue)
     */
    var redeliveryDuration = Duration.ofDays(1)

    /**
     * Expiry duration for dead letter queue
     */
    var deadLetterExpiration = Duration.ofDays(14)

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

        val pa = brokerService.persistenceAdapter as KahaDBPersistenceAdapter
        pa.isCheckForCorruptJournalFiles = true
        pa.isIgnoreMissingJournalfiles = false
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

        //region Broker plugins
        brokerService.plugins = arrayOf(
                //region Authentication plugin
                SimpleAuthenticationPlugin().also { pAuth ->

                    // Users
                    val users = ArrayList<AuthenticationUser>()
                    users.add(AuthenticationUser(this.user!!.userName, this.user!!.password, this.user!!.groupName))
                    pAuth.setUsers(users)

                },
                //endregion

                // region Authorization plugin
                AuthorizationPlugin().also { p ->
                    p.map = DefaultAuthorizationMap().also { authzMap ->
                        val group = this.user?.groupName
                            ?: throw IllegalArgumentException("Group name is empty, but mandatory for setting appropriate permissions")

                        authzMap.setAuthorizationEntries(
                                listOf(
                                        // Leoz group, all access
                                        AuthorizationEntry().also {
                                            it.setTopic(">")
                                            it.setAdmin(group)
                                            it.setRead(group)
                                            it.setWrite(group)
                                        },
                                        AuthorizationEntry().also {
                                            it.setQueue(">")
                                            it.setAdmin(group)
                                            it.setRead(group)
                                            it.setWrite(group)
                                        },
                                        // Leoz group, all access to temp destinations
                                        TempDestinationAuthorizationEntry().also {
                                            it.setTopic(">")
                                            it.setAdmin(group)
                                            it.setRead(group)
                                            it.setWrite(group)
                                        },
                                        TempDestinationAuthorizationEntry().also {
                                            it.setQueue(">")
                                            it.setAdmin(group)
                                            it.setRead(group)
                                            it.setWrite(group)
                                        }
                                )
                        )
                    }
                },
                //endregion

                //region Redelivery plugin
                RedeliveryPlugin().also { p ->
                    p.isFallbackToDeadLetter = true
                    p.isSendToDlqIfMaxRetriesExceeded = true

                    p.redeliveryPolicyMap = RedeliveryPolicyMap().also {
                        it.defaultEntry = RedeliveryPolicy().also { rp ->
                            rp.isUseExponentialBackOff = true
                            rp.initialRedeliveryDelay = Duration.ofSeconds(5).toMillis()
                            rp.backOffMultiplier = 2.0
                            rp.maximumRedeliveryDelay = Duration.ofMinutes(15).toMillis()

                            val maxRedeliveryTime = this.redeliveryDuration.toMillis()

                            rp.maximumRedeliveries = Math.ceil(
                                    // Calculate maximum redeliveries from above parameters
                                    (maxRedeliveryTime / rp.maximumRedeliveryDelay) +
                                            // Add approximate retries for exponential backoff
                                            (Math.log(rp.maximumRedeliveryDelay.toDouble() / rp.initialRedeliveryDelay) / Math.log(rp.backOffMultiplier))
                            ).toInt()
                        }
                    }
                    //endregion
                },
                //endregion

                //region Custom notification plüugin
                object : BrokerPluginSupport() {

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
                //endregion
        )
        //endregion

        //region Virtual destination setup
        fun createDestinationInterceptors(): List<DestinationInterceptor> {
            /**
             * This method is a replica of the protected {@link BrokerService.createDefaultDestinationIntercept}
             * @param compositeDestinations Additional composite destinations
             */
            fun createDefaultDestinationInterceptors(compositeDestinations: List<CompositeDestination> = listOf()): List<DestinationInterceptor> {
                // Add default virtual destinations (replicated from {@link BrokerService.createDefaultDestinationIntercept}
                val answer = ArrayList<DestinationInterceptor>()
                if (brokerService.isUseVirtualTopics) {
                    val interceptor = VirtualDestinationInterceptor()
                    val virtualTopic = VirtualTopic()
                    virtualTopic.name = "VirtualTopic.>"
                    val virtualDestinations = mutableListOf<VirtualDestination>(virtualTopic)

                    // masc201710.12 Add our own custom composite destinations
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

            return createDefaultDestinationInterceptors(
                    compositeDestinations = this.compositeDestinations)
        }

        brokerService.destinationInterceptors = createDestinationInterceptors().toTypedArray()
        //endregion

        //region Default destination policy and DLQ strategy
        brokerService.destinationPolicy = PolicyMap().also { pm ->
            pm.defaultEntry = PolicyEntry().also { pe ->
                pe.deadLetterStrategy = SharedDeadLetterStrategy().also { ds ->
                    // Setup DLQ expiration to avoid infinte DLQ growth
                    ds.expiration = this.deadLetterExpiration.toMillis()
                    ds.isProcessExpired = false
                    ds.isProcessNonPersistent = false
                }
            }
        }
        //endregion

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
