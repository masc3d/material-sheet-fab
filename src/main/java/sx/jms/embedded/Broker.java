package sx.jms.embedded;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;
import sx.event.EventDelegate;
import sx.event.EventDispatcher;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masc on 01.06.15.
 */
public abstract class Broker implements Disposable {
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
     * Broker user
     */
    public static class User {
        private String mUserName;
        private String mPassword;
        private String mGroupName;

        public User(String userName, String password, String groupName) {
            mUserName = userName;
            mPassword = password;
            mGroupName = groupName;
        }

        public String getUserName() {
            return mUserName;
        }

        public String getPassword() {
            return mPassword;
        }

        public String getGroupName() {
            return mGroupName;
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

    /**
     * Broker name
     */
    private String mBrokerName;
    /** Native tcp port for this broker to listen to */
    private Integer mNativeTcpPort;
    /** Peer brokers */
    private List<PeerBroker> mPeerBrokers = new ArrayList();
    /*+ Broker users */
    private User mUser;
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
    public interface EventListener extends sx.event.EventListener {
        void onStart();
        void onStop();
    }

    /** Broker event dispatcher/delegate */
    private EventDispatcher<EventListener> mListenerEventDispatcher = EventDispatcher.createThreadSafe();

    public EventDelegate<EventListener> getDelegate() {
        return mListenerEventDispatcher;
    }
    //endregion

    //region Interface
    protected abstract void startImpl() throws Exception;

    protected abstract void stopImpl() throws Exception;

    protected abstract boolean isStartedImpl();

    /** Create jms message queue */
    public abstract Queue createQueue(String name);
    /** Create jms message topic */
    public abstract Topic createTopic(String name);
    /** Jms connection factory */
    public abstract ConnectionFactory getConnectionFactory();
    //endregion

    /**
     * Start broker
     * @throws Exception
     */
    public synchronized final void start() throws Exception {
        this.startImpl();
        mListenerEventDispatcher.emit(new EventDispatcher.Runnable<EventListener>() {
            @Override
            public void run(EventListener listener) {
                listener.onStart();
            }
        });
    }

    /**
     * Stop broker
     * @throws Exception
     */
    public synchronized final void stop() throws Exception {
        if (this.isStarted()) {
            mListenerEventDispatcher.emit(new EventDispatcher.Runnable<EventListener>() {
                @Override
                public void run(EventListener listener) {
                    listener.onStop();
                }
            });
        }
        this.stopImpl();
    }

    public boolean isStarted() {
        return this.isStartedImpl();
    }

    public String getBrokerName() {
        return mBrokerName;
    }

    public void setBrokerName(String brokerName) {
        mBrokerName = brokerName;
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

    /**
     * Add broker user
     * @param user
     */
    public void setUser(User user) { mUser = user; }

    public User getUser() {
        return mUser;
    }

    @Override
    public void close() {
        try {
            this.stop();
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
