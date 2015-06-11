package org.deku.leo2.node.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.inject.Named;

/**
 * Created by masc on 11.06.15.
 */
@Named
@ConfigurationProperties(prefix="broker")
public class BrokerSettings {
    private Integer mNativePort;
    private String mHttpContextPath;

    public Integer getNativePort() {
        return mNativePort;
    }

    public String getHttpContextPath() {
        return mHttpContextPath;
    }
}
