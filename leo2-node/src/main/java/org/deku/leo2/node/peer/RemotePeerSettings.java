package org.deku.leo2.node.peer;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.inject.Named;

/**
 * Created by masc on 11.06.15.
 */
@Named
@ConfigurationProperties(prefix="remote")
public class RemotePeerSettings {
    public class Broker {

        private Integer mNativePort;
        private String mHttpPath;

        public Integer getNativePort() {
            return mNativePort;
        }

        public void setNativePort(Integer nativePort) {
            mNativePort = nativePort;
        }

        public String getHttpPath() {
            return mHttpPath;
        }

        public void setHttpPath(String httpPath) {
            mHttpPath = httpPath;
        }
    }

    private String mHost;
    private Integer mHttpPort;
    private String mHttpPath;
    private Broker mBroker = new Broker();

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        mHost = host;
    }

    public Integer getHttpPort() {
        return mHttpPort;
    }

    public void setHttpPort(Integer httpPort) {
        mHttpPort = httpPort;
    }

    public String getHttpPath() {
        return mHttpPath;
    }

    public void setHttpPath(String httpPath) {
        mHttpPath = httpPath;
    }

    public Broker getBroker() {
        return mBroker;
    }

    public void setBroker(Broker broker) {
        mBroker = broker;
    }
}
