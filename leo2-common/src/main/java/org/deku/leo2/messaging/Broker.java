package org.deku.leo2.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;
import sx.event.EventDelegate;
import sx.event.EventDispatcher;
import sx.event.EventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by masc on 01.06.15.
 */
public abstract class Broker implements Disposable {
    public final static String USERNAME = "leo2";
    public final static String PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC";

    /** Log */
    protected Log mLog = LogFactory.getLog(this.getClass());

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

    /** Native tcp port for this broker to listen to */
    private Integer mNativeTcpPort;
    /** Peer brokers */
    private List<PeerBroker> mPeerBrokers = new ArrayList<>();
    /** Data directory for store */
    private File mDataDirectory;

    /**
     * c'tor for derived classes to provide defaults
     * @param nativeTcpPort
     */
    protected Broker(Integer nativeTcpPort) {
        mNativeTcpPort = nativeTcpPort;
    }

    //region Events

    /** Broker event listener interface */
    public interface Listener extends EventListener {
        void onStart();
    }

    /** Broker event dispatcher/delegate */
    private EventDispatcher<Listener> mListenerEventDispatcher = EventDispatcher.createThreadSafe();

    public EventDelegate<Listener> getListenerEventDispatcher() {
        return mListenerEventDispatcher;
    }
    //endregion

    //region Interface
    protected abstract void startImpl() throws Exception;

    protected abstract void stopImpl() throws Exception;

    protected abstract boolean isStartedImpl();
    //endregion

    /**
     * Start broker
     * @throws Exception
     */
    public synchronized final void start() throws Exception {
        this.startImpl();
        mListenerEventDispatcher.emit(Listener::onStart);
    }

    /**
     * Stop broker
     * @throws Exception
     */
    public synchronized final void stop() throws Exception {
        this.stopImpl();
    }

    public synchronized boolean isStarted() {
        return this.isStartedImpl();
    }

    /** Broker data directory */
    public File getDataDirectory() {
        return mDataDirectory;
    }

    /**
     * Set broker data directory.
     * @param file Data directory
     */
    public final void setDataDirectory(File file) {
        mDataDirectory = file;
    }

    /** Broker native protocol tcp port */
    public Integer getNativeTcpPort() {
        return mNativeTcpPort;
    }

    /**
     * Set broker native tcp port.
     * @param nativeTcpPort
     */
    public void setNativeTcpPort(Integer nativeTcpPort) {
        mNativeTcpPort = nativeTcpPort;
    }

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

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
