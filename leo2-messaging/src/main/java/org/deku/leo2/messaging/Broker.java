package org.deku.leo2.messaging;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import sx.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton instance for leo2 embedded brokers
 * Created by masc on 16.04.15.
 */
public class Broker implements Disposable {
    /**
     * Peer broker
     */
    private class PeerBroker {
        public String hostname;
        public Integer httpPort;
    }

    /**
     * Logger
     */
    private Logger mLogger = Logger.getLogger(Broker.class.getName());
    /**
     * The native broker service
     */
    private BrokerService mBrokerService;
    /**
     * List of peer brokers
     */
    List<PeerBroker> mPeerBrokers;

    //region Singleton
    /**
     * Singleton instance
     */
    private static final Broker mBroker = new Broker();
    /**
     * Singleton accessor
     */
    public static Broker getInstance() {
        return mBroker;
    }
    //endregion

    /**
     * c'tor
     */
    private Broker() {
        mPeerBrokers = new ArrayList<PeerBroker>();
    }

    /**
     * Start broker
     * @throws Exception
     */
    public synchronized void start() throws Exception {
        this.stop();

        mBrokerService = new BrokerService();
        // All embedded brokers will listen on openwire and http
        mBrokerService.addConnector(Util.createActiveMqUri("localhost", false));
        mBrokerService.addConnector(Util.createActiveMqUri("localhost", 8080, false));

        for (PeerBroker pb : mPeerBrokers) {
            NetworkConnector nc = mBrokerService.addNetworkConnector(Util.createActiveMqUri(pb.hostname, pb.httpPort, true));
            nc.setDuplex(true);
            mBrokerService.addNetworkConnector(nc);
        }
        mBrokerService.start();
    }

    /**
     * Stop broker
     * @throws Exception
     */
    public synchronized void stop() throws Exception {
        if (mBrokerService != null)
            mBrokerService.stop();
    }

    /**
     * Adds a peer broker to this broker's configuration
     * This setting needs to be set before starting the broker
     * or the broker has to be restarted for the change to take effect.
     * @param hostname Hostname of the peer broker
     * @param httpPort Optional http port. If null, the native port/protocol will be used
     */
    public void addPeerBroker(String hostname, Integer httpPort) {
        PeerBroker pb = new PeerBroker();
        pb.hostname = hostname;
        pb.httpPort = httpPort;
        mPeerBrokers.add(pb);
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch (Exception e) {
            mLogger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
