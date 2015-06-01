package org.deku.leo2.messaging;

import sx.Disposable;

/**
 * Created by masc on 01.06.15.
 */
public interface Broker extends Disposable {
    void start() throws Exception;

    void stop() throws Exception;

    void addPeerBroker(String hostname, Integer httpPort);
}
