package sx.jms.embedded.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.broker.util.RedeliveryPlugin;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.*;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.transport.TransportServer;
import sx.LazyInstance;
import sx.jms.embedded.Broker;

import javax.jms.ConnectionFactory;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import javax.jms.Topic;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Broker implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMQBroker extends Broker {
    //region Singleton
    private static LazyInstance<ActiveMQBroker> mInstance = new LazyInstance(new Supplier() {
        @Override
        public Object get() {
            return new ActiveMQBroker();
        }
    });

    public static ActiveMQBroker instance() {
        return mInstance.get();
    }
    //endregion

    // Defaults
    private static final int NATIVE_TCP_PORT = 61616;

    /** Native broker service */
    private volatile BrokerService mBrokerService;

    /** External transport servers, eg. servlets */
    List<TransportServer> mExternalTransportServers = new ArrayList<>();

    /** Url for establishing connection to local/embedded broker */
    private URI mLocalUri;

    /** Connection factory for connecting to the embedded broker */
    private ConnectionFactory mConnectionFactory;

    /** c'tor */
    private ActiveMQBroker() {
        super(NATIVE_TCP_PORT);

        try {
            mLocalUri = new URI("vm://localhost?create=false");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add transport server, eg. from servlet
     * @return
     */
    public void addConnector(TransportServer transportServer) throws Exception {
        mExternalTransportServers.add(transportServer);
    }

    /**
     * For (performance) testing purposes: override local/embedded connection URI.
     * Must be called before connection factory is retrieved for the first time.
     * @param uri
     */
    public void setLocalUri(URI uri) {
        mLocalUri = uri;
    }

    /**
     * Start broker
     * @throws Exception
     */
    @Override
    protected void startImpl() throws Exception {
        this.stop();

        if (this.getUser() == null)
            throw new IllegalStateException("Broker user not set");

        // Broker initialization
        mBrokerService = new BrokerService();
        mBrokerService.setDataDirectoryFile(this.getDataDirectory());
        // Required for redelivery plugin/policy
        mBrokerService.setSchedulerSupport(true);
        // Disabling activemq's integrated  shutdown hook, favoring consumer side ordered shutdown
        mBrokerService.setUseShutdownHook(false);

        // Persistence setup
        KahaDBPersistenceAdapter pa = (KahaDBPersistenceAdapter)mBrokerService.getPersistenceAdapter();
        pa.setCheckForCorruptJournalFiles(true);

        // Create VM broker for direct (in memory/vm) connections.
        // The Broker name has to match for clients to connect
        mBrokerService.addConnector("vm://localhost");

        // Statically defined transport connectors for native clients to connect to
        mBrokerService.addConnector(String.format("tcp://0.0.0.0:%d",
                this.getNativeTcpPort()));

        // Peer/network connectors for brokers to inter-connect
        for (PeerBroker pb : this.getPeerBrokers()) {
            URI hostUrl = createUri(pb, false);
            NetworkConnector nc = new DiscoveryNetworkConnector(URI.create(String.format("static:(%s)", hostUrl)));
            nc.setUserName(this.getUser().getUserName());
            nc.setPassword(this.getUser().getPassword());
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        for (TransportServer ts : mExternalTransportServers) {
            mBrokerService.addConnector(ts);
        }

        // Broker plugins
        List<BrokerPlugin> brokerPlugins = new ArrayList();

        //region Authentication
        SimpleAuthenticationPlugin pAuth = new SimpleAuthenticationPlugin();

        // Users
        List<AuthenticationUser> users = new ArrayList();
        users.add(new AuthenticationUser(this.getUser().getUserName(), this.getUser().getPassword(), this.getUser().getGroupName()));
        pAuth.setUsers(users);

        brokerPlugins.add(pAuth);
        //endregion

        //region Authorizations
        List<DestinationMapEntry> authzEntries = new ArrayList();

        String group = this.getUser().getGroupName();

        AuthorizationEntry pAuthzEntry;
        // Leo group, all access
        pAuthzEntry = new AuthorizationEntry();
        pAuthzEntry.setTopic(">");
        pAuthzEntry.setAdmin(group);
        pAuthzEntry.setRead(group);
        pAuthzEntry.setWrite(group);
        authzEntries.add(pAuthzEntry);

        pAuthzEntry = new AuthorizationEntry();
        pAuthzEntry.setQueue(">");
        pAuthzEntry.setAdmin(group);
        pAuthzEntry.setRead(group);
        pAuthzEntry.setWrite(group);
        authzEntries.add(pAuthzEntry);

        // Leo group, all access to temp destinations
        pAuthzEntry = new TempDestinationAuthorizationEntry();
        pAuthzEntry.setTopic(">");
        pAuthzEntry.setAdmin(group);
        pAuthzEntry.setRead(group);
        pAuthzEntry.setWrite(group);
        authzEntries.add(pAuthzEntry);

        pAuthzEntry = new TempDestinationAuthorizationEntry();
        pAuthzEntry.setQueue(">");
        pAuthzEntry.setAdmin(group);
        pAuthzEntry.setRead(group);
        pAuthzEntry.setWrite(group);
        authzEntries.add(pAuthzEntry);

        // Create authorization map from entries
        DefaultAuthorizationMap authzMap = new DefaultAuthorizationMap();
        authzMap.setAuthorizationEntries(authzEntries);

        // Authorization plugin
        AuthorizationPlugin pAuthz = new AuthorizationPlugin();
        pAuthz.setMap(authzMap);

        brokerPlugins.add(pAuthz);
        //endregion

        //region Redelivery policy
        RedeliveryPlugin pRedelivery = new RedeliveryPlugin();
        // TODO: verify if those plugin options are really needed
        pRedelivery.setFallbackToDeadLetter(false);
        pRedelivery.setSendToDlqIfMaxRetriesExceeded(false);

        RedeliveryPolicyMap rpm = new RedeliveryPolicyMap();

        // TODO: define sensible values for redelivery of messages
        RedeliveryPolicy rp = new RedeliveryPolicy();
        rp.setMaximumRedeliveries(-1);
        rp.setInitialRedeliveryDelay(2000);
//        rp.setBackOffMultiplier(2);
//        rp.setUseExponentialBackOff(true);

        rpm.setDefaultEntry(rp);
        pRedelivery.setRedeliveryPolicyMap(rpm);
        //endregion

        brokerPlugins.add(pRedelivery);

        mBrokerService.setPlugins(brokerPlugins.toArray(new BrokerPlugin[0]));

        try {
            mBrokerService.start();
        } catch(Exception e) {
            try {
                mBrokerService.stop();
            } catch(Exception e2) {
                mLog.error(e2.getMessage(), e2);
            }
            throw e;
        }
    }

    /**
     * Stop broker
     * @throws Exception
     */
    @Override
    protected void stopImpl() throws Exception {
        if (mBrokerService != null && mBrokerService.isStarted()) {
            mBrokerService.stop();
            mBrokerService = null;
        }
    }

    @Override
    protected boolean isStartedImpl() {
        BrokerService brokerService = mBrokerService;
        return brokerService != null && brokerService.isStarted();
    }

    @Override
    public Queue createQueue(String name) {
        return new ActiveMQQueue(name);
    }

    @Override
    public Topic createTopic(String name) { return new ActiveMQTopic(name); }

    @Override
    public ConnectionFactory getConnectionFactory() {
        if (mConnectionFactory == null) {
            PooledConnectionFactory psf = new PooledConnectionFactory();
            psf.setConnectionFactory(new ActiveMQConnectionFactory(
                    this.getUser().getUserName(),
                    this.getUser().getPassword(),
                    // Explicitly do _not_ create (another) embedded broker on connection, just in case
                    mLocalUri.toString()));
            mConnectionFactory = psf;
        }
        return mConnectionFactory;
    }

    /**
     * Create ActiveMQ URI
     * @param peerBroker Peer broker
     * @param failover
     * @return ActiveMQ URI
     */
    public static URI createUri(PeerBroker peerBroker, boolean failover) {
        String scheme = peerBroker.getTransportType().toString();
        String path = peerBroker.getHttpPath();
        if (path == null)
            path = "";
        Integer port = peerBroker.getPort();
        if (port == null) {
            port = (peerBroker.getTransportType() == TransportType.TCP) ? 61616 : 80;
        }

        if (failover)
            scheme = "failover:" + scheme;

        try {
            return new URI(String.format("%s://%s:%d%s",
                    scheme,
                    peerBroker.getHostname(),
                    port,
                    path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
