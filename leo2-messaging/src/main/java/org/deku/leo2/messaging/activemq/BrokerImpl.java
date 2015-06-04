package org.deku.leo2.messaging.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.transport.TransportServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import sx.LazyInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton instance for leo2 embedded brokers
 * Created by masc on 16.04.15.
 */
public class BrokerImpl implements Broker {
    /**
     * Peer broker
     */
    private class PeerBroker {
        public String hostname;
        public Integer httpPort;
    }

    private static LazyInstance<BrokerImpl> mInstance = new LazyInstance<>(() -> new BrokerImpl());
    /**
     * Singleton accessor
     */
    public static BrokerImpl getInstance() {
        return mInstance.get();
    }

    /** Logger */
    private Log mLog = LogFactory.getLog(BrokerImpl.class);
    /** Native broker service */
    private BrokerService mBrokerService;

    /** Data directory for store */
    private File mDataDirectory;

    /** List of peer brokers */
    List<PeerBroker> mPeerBrokers = new ArrayList<>();
    List<TransportServer> mExternalTransportServers = new ArrayList<>();

    /**
     * c'tor
     */
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

        mBrokerService = new BrokerService();
        mBrokerService.setDataDirectoryFile(mDataDirectory);

        // All embedded brokers will listen on openwire and http
        mBrokerService.addConnector(Util.createUri("localhost", false));
        //mBrokerService.addConnector(Util.createUri("localhost", 8080, false));

        for (PeerBroker pb : mPeerBrokers) {
            NetworkConnector nc = mBrokerService.addNetworkConnector(Util.createUri(pb.hostname, pb.httpPort, true));
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        for (TransportServer ts : mExternalTransportServers) {
            mBrokerService.addConnector(ts);
        }
        mBrokerService.start();
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
