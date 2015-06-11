package org.deku.leo2.messaging.activemq;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.*;
import org.apache.activemq.transport.TransportServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import sx.LazyInstance;
import sx.event.EventDelegate;
import sx.event.EventDispatcher;
import sx.event.EventListener;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Broker implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMqBroker extends Broker {
    //region Singleton
    private static LazyInstance<ActiveMqBroker> mInstance = new LazyInstance<>(ActiveMqBroker::new);

    public static ActiveMqBroker instance() {
        return mInstance.get();
    }
    //endregion

    /** Log */
    private Log mLog = LogFactory.getLog(ActiveMqBroker.class);

    /** Native broker service */
    private BrokerService mBrokerService;

    /** External transport servers, eg. servlets */
    List<TransportServer> mExternalTransportServers = new ArrayList<>();

    /** c'tor */
    private ActiveMqBroker() {
    }

    /**
     * Add transport server, eg. from servlet
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
    protected void startImpl() throws Exception {
        this.stop();

        // Broker initialization
        mBrokerService = new BrokerService();
        mBrokerService.setDataDirectoryFile(this.getDataDirectory());

        // Statically defined transport connectors for native clients to connect to
        mBrokerService.addConnector(String.format("tcp://0.0.0.0:61616"));

        // Create VM broker for direct (in memory/vm) connections.
        // The Broker name has to match for clients to connect
        mBrokerService.addConnector("vm://localhost");

        // Peer/network connectors for brokers to inter-connect
        for (PeerBroker pb : this.getPeerBrokers()) {
            URI hostUrl = createUri(pb, false);
            NetworkConnector nc = new DiscoveryNetworkConnector(URI.create(String.format("static:(%s)", hostUrl)));
            nc.setUserName(USERNAME);
            nc.setPassword(PASSWORD);
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        for (TransportServer ts : mExternalTransportServers) {
            mBrokerService.addConnector(ts);
        }

        // Broker plugins
        List<BrokerPlugin> brokerPlugins = new ArrayList<>();

        //region Authentication
        SimpleAuthenticationPlugin pAuth = new SimpleAuthenticationPlugin();

        // Users
        List<AuthenticationUser> users = new ArrayList<>();
        String GROUP_LEO = "leo2";
        users.add(new AuthenticationUser(USERNAME, PASSWORD, GROUP_LEO));
        pAuth.setUsers(users);

        brokerPlugins.add(pAuth);
        //endregion

        //region Authorizations
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

        brokerPlugins.add(pAuthz);
        //endregion

        mBrokerService.setPlugins(brokerPlugins.toArray(new BrokerPlugin[0]));
        mBrokerService.start();
    }

    /**
     * Stop broker
     * @throws Exception
     */
    @Override
    protected void stopImpl() throws Exception {
        if (mBrokerService != null) {
            mBrokerService.stop();
            mBrokerService = null;
        }
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
