package org.deku.leo2.node.messaging;

import com.google.common.base.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.node.peer.PeerSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by masc on 11.06.15.
 */
@Configuration
@ConfigurationProperties(prefix="broker")
@Lazy(false)
public class BrokerConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    @Inject
    PeerSettings mPeerSettings;

    private Integer mNativePort;
    private String mHttpContextPath;

    public Integer getNativePort() {
        return mNativePort;
    }

    public void setNativePort(Integer nativePort) {
        mNativePort = nativePort;
    }

    public String getHttpContextPath() {
        return mHttpContextPath;
    }

    public void setHttpContextPath(String httpContextPath) {
        mHttpContextPath = httpContextPath;
    }

    @PostConstruct
    public void initialize() throws Exception {

        //region Setup message broker
        // Broker configuration, must occur before tunnel servlet starts
        mLog.info("Configuring messaging broker");
        ActiveMQBroker.instance().setNativeTcpPort(this.getNativePort());

        if (!Strings.isNullOrEmpty(mPeerSettings.getHost())) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            mLog.info(String.format("Adding peer broker: %s", mPeerSettings.getHost()));

            ActiveMQBroker.instance().addPeerBroker(new Broker.PeerBroker(
                    mPeerSettings.getHost(),
                    Broker.TransportType.TCP,
                    null));
        }
        //endregion
    }
}
