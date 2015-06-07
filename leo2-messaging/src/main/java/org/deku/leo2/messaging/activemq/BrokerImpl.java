package org.deku.leo2.messaging.activemq;

import com.google.common.collect.Lists;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.*;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.discovery.DiscoveryTransport;
import org.apache.activemq.transport.discovery.http.HTTPDiscoveryAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AUTH;
import org.deku.leo2.messaging.Broker;
import sx.LazyInstance;
import sx.util.EventDelegate;
import sx.util.EventDispatcher;
import sx.util.EventListener;

import java.io.File;
import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton instance for leo2 embedded brokers
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

    //region Singleton
    private static LazyInstance<BrokerImpl> mInstance = new LazyInstance<>(() -> new BrokerImpl());

    /**
     * Singleton accessor
     */
    public static BrokerImpl getInstance() {
        return mInstance.get();
    }
    //endregion

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
    private BrokerImpl() {
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
        mBrokerService.addConnector(Util.createUri("0.0.0.0", false));

        // Peer/network connectors for brokers to inter-connect
        for (PeerBroker pb : mPeerBrokers) {
            NetworkConnector nc = mBrokerService.addNetworkConnector(Util.createUri(pb.hostname, pb.httpPort, true));
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        for (TransportServer ts : mExternalTransportServers) {
            mBrokerService.addConnector(ts);
        }

        // Broker plugins
        List<BrokerPlugin> brokerPlugins = new ArrayList<>();;

        // Authentication
        SimpleAuthenticationPlugin pAuth = new SimpleAuthenticationPlugin();

        // Users
        List<AuthenticationUser> users = new ArrayList<>();
        String GROUP_LEO = "leo2";
        users.add(new AuthenticationUser(LEO2_USERNAME, LEO2_PASSWORD, GROUP_LEO));
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

        // Authorization map
        DefaultAuthorizationMap authzMap = new DefaultAuthorizationMap();
        authzMap.setAuthorizationEntries(authzEntries);

        // Authorization plugin
        AuthorizationPlugin pAuthz = new AuthorizationPlugin();
        pAuthz.setMap(authzMap);

        // Register plugins
        brokerPlugins.add(pAuth);
        brokerPlugins.add(pAuthz);
        mBrokerService.setPlugins(brokerPlugins.toArray(new BrokerPlugin[0]));

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
}
