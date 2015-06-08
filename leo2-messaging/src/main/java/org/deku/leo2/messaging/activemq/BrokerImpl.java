package org.deku.leo2.messaging.activemq;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.*;
import org.apache.activemq.transport.TransportServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import sx.util.EventDelegate;
import sx.util.EventDispatcher;
import sx.util.EventListener;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Broker implementation for activemq
 * Created by masc on 16.04.15.
 */
public class BrokerImpl extends Broker {
    /**
     * Peer broker
     */
    private class PeerBroker {
        public String hostname;
        public Integer httpPort;
    }

    //region Events
    /**
     * Broker event listener interface
     */
    public interface Listener extends EventListener {
        void onStart();
    }

    /**
     * Broker event dispatcher/delegate
     */
    private EventDispatcher<Listener> mListenerEventDispatcher = EventDispatcher.createThreadSafe();

    public EventDelegate<Listener> getListenerEventDispatcher() {
        return mListenerEventDispatcher;
    }
    //endregion

    /** Log */
    private Log mLog = LogFactory.getLog(BrokerImpl.class);

    /** Native broker service */
    private BrokerService mBrokerService;

    /** Data directory for store */
    private File mDataDirectory;

    /** List of peer brokers */
    List<PeerBroker> mPeerBrokers = new ArrayList<>();
    List<TransportServer> mExternalTransportServers = new ArrayList<>();

    /** c'tor */
    public BrokerImpl() {
    }

    /**
     * Active MQ broker service instance
     * @return
     */
    public void addConnector(TransportServer transportServer) throws Exception {
        mExternalTransportServers.add(transportServer);
    }

    /**
     * Start broker
     * @throws Exception
     */
    @Override
    public synchronized void start() throws Exception {
        this.stop();

        // Broker initialization
        mBrokerService = new BrokerService();
        mBrokerService.setDataDirectoryFile(mDataDirectory);

        // Statically defined transport connectors for clients to connect to
        mBrokerService.addConnector(createUri("0.0.0.0", false));
        // Create VM broker for direct (in memory/vm) connections.
        // The Broker name has to match for clients to connect
        mBrokerService.addConnector("vm://" + NAME);

        // Peer/network connectors for brokers to inter-connect
        for (PeerBroker pb : mPeerBrokers) {
            NetworkConnector nc = mBrokerService.addNetworkConnector(createUri(pb.hostname, pb.httpPort, true));
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        for (TransportServer ts : mExternalTransportServers) {
            mBrokerService.addConnector(ts);
        }

        // Broker plugins
        List<BrokerPlugin> brokerPlugins = new ArrayList<>();

        // Authentication
        SimpleAuthenticationPlugin pAuth = new SimpleAuthenticationPlugin();

        // Users
        List<AuthenticationUser> users = new ArrayList<>();
        String GROUP_LEO = "leo2";
        users.add(new AuthenticationUser(USERNAME, PASSWORD, GROUP_LEO));
        pAuth.setUsers(users);

        // Authorizations
        List<DestinationMapEntry> authzEntries = new ArrayList<>();

        AuthorizationEntry pAuthzEntry = new AuthorizationEntry();

        // Leo group, all access
        pAuthzEntry = new AuthorizationEntry();
        pAuthzEntry.setTopic(">");
        pAuthzEntry.setAdmin(GROUP_LEO);
        pAuthzEntry.setRead(GROUP_LEO);
        pAuthzEntry.setWrite(GROUP_LEO);
        authzEntries.add(pAuthzEntry);

        pAuthzEntry = new AuthorizationEntry();
        pAuthzEntry.setQueue(">");
        pAuthzEntry.setAdmin(GROUP_LEO);
        pAuthzEntry.setRead(GROUP_LEO);
        pAuthzEntry.setWrite(GROUP_LEO);
        authzEntries.add(pAuthzEntry);

        // Leo group, all access to temp destinations
        pAuthzEntry = new TempDestinationAuthorizationEntry();
        pAuthzEntry.setTopic(">");
        pAuthzEntry.setAdmin(GROUP_LEO);
        pAuthzEntry.setRead(GROUP_LEO);
        pAuthzEntry.setWrite(GROUP_LEO);
        authzEntries.add(pAuthzEntry);

        pAuthzEntry = new TempDestinationAuthorizationEntry();
        pAuthzEntry.setQueue(">");
        pAuthzEntry.setAdmin(GROUP_LEO);
        pAuthzEntry.setRead(GROUP_LEO);
        pAuthzEntry.setWrite(GROUP_LEO);
        authzEntries.add(pAuthzEntry);

        // Create authorization map from entries
        DefaultAuthorizationMap authzMap = new DefaultAuthorizationMap();
        authzMap.setAuthorizationEntries(authzEntries);

        // Authorization plugin
        AuthorizationPlugin pAuthz = new AuthorizationPlugin();
        pAuthz.setMap(authzMap);

        // Register plugins
        brokerPlugins.add(pAuth);
        brokerPlugins.add(pAuthz);
        mBrokerService.setPlugins(brokerPlugins.toArray(new BrokerPlugin[0]));

        mBrokerService.setBrokerName(Broker.NAME);
        mBrokerService.start();

        mListenerEventDispatcher.emit(Listener::onStart);
    }

    /**
     * Stop broker
     * @throws Exception
     */
    @Override
    public synchronized void stop() throws Exception {
        if (mBrokerService != null) {
            mBrokerService.stop();
            mBrokerService = null;
        }
    }

    /**
     * Adds a peer broker to this broker's configuration
     * This setting needs to be set before starting the broker
     * or the broker has to be restarted for the change to take effect.
     * @param hostname Hostname of the peer broker
     * @param httpPort Optional http port. If null, the native port/protocol will be used
     */
    @Override
    public void addPeerBroker(String hostname, Integer httpPort) {
        PeerBroker pb = new PeerBroker();
        pb.hostname = hostname;
        pb.httpPort = httpPort;
        mPeerBrokers.add(pb);
    }

    @Override
    public void setDataDirectory(File file) {
        mDataDirectory = file;
    }

    public File getDataDirectory() {
        return mDataDirectory;
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    /**
     * Create ActiveMQ URI
     * @param hostname Hostname
     * @param httpPort Optional http port, if omitted native port will be used
     * @return ActiveMQ URI
     */
    public static URI createUri(String hostname, Integer httpPort, boolean failover) {
        String scheme;
        String path = (httpPort != null) ? "/leo2/jms" : "";

        int port;
        if (httpPort != null) {
            scheme = "http";
            port = httpPort;
        } else {
            scheme = "tcp";
            port = 61616;
        }

        if (failover)
            scheme = "failover:" + scheme;

        try {
            return new URI(String.format("%s://%s:%d%s", scheme, hostname, port, path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create native ActiveMQ URI
     * @param hostname
     * @return
     */
    public static URI createUri(String hostname, boolean failover) {
        return createUri(hostname, null, failover);
    }
}
