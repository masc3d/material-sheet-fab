package org.deku.leo2.messaging.activemq;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by masc on 17.04.15.
 */
class Util {
    /**
     * Create ActiveMQ URI
     * @param hostname Hostname
     * @param httpPort Optional http port, if omitted native port will be used
     * @return ActiveMQ URI
     */
    public static URI createUri(String hostname, Integer httpPort, boolean failover) {
        String scheme;
        String path = (httpPort != null) ? "/jms" : "";

        int port;
        if (httpPort != null) {
            scheme = "http";
            port = httpPort;
        } else {
            scheme = "tcp";
            port = 61616;
        }

        if (failover)
            scheme = "failover:" + scheme;

        try {
            return new URI(String.format("%s://%s:%d%s", scheme, hostname, port, path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create native ActiveMQ URI
     * @param hostname
     * @return
     */
    public static URI createUri(String hostname, boolean failover) {
        return createUri(hostname, null, failover);
    }
}
