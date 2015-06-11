package org.deku.leo2.messaging;

import sx.Disposable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masc on 01.06.15.
 */
public abstract class Broker implements Disposable {
    /**
     * Transport type
     */
    public enum TransportType {
        HTTP("http"),
        TCP("tcp");

        TransportType(String transportType) {
            mTransportType = transportType;
        }

        private String mTransportType;

        @Override
        public String toString() {
            return mTransportType;
        }
    }

    /**
     * Peer broker
     */
    public static class PeerBroker {
        private String mHostname;
        private TransportType mTransportType;
        private String mHttpPath;

        public PeerBroker(String hostname, TransportType transportType, Integer port, String httpPath) {
            if (transportType != TransportType.HTTP && httpPath != null) {
                throw new IllegalArgumentException("Http path not valid for non-http transports");
            }

            mHostname = hostname;
            mTransportType = transportType;
            mPort = port;
        }
        public PeerBroker(String hostname, TransportType transportType, Integer port) {
            this(hostname, transportType, port, null);
        }

        public Integer getPort() {

            return mPort;
        }

        public TransportType getTransportType() {
            return mTransportType;
        }

        public String getHostname() {
            return mHostname;
        }

        public String getHttpPath() {
            return mHttpPath;
        }

        private Integer mPort;
    }

    public final static String USERNAME = "leo2";
    public final static String PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC";

    private List<PeerBroker> mPeerBrokers = new ArrayList<>();

    /**
     * Set broker data directory
     * @param file Data directory
     */
    public abstract void setDataDirectory(File file);

    /**
     * Add peer (network transport) broker
     * @param peerBroker Peer broker
     */
    public void addPeerBroker(PeerBroker peerBroker) {
        mPeerBrokers.add(peerBroker);
    }

    public List<PeerBroker> getPeerBrokers() {
        return mPeerBrokers;
    }

    /** Start broker */
    public abstract void start() throws Exception;
    /** Stop broker */
    public abstract void stop() throws Exception;
}
