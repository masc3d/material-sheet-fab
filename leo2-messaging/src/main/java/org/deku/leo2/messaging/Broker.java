package org.deku.leo2.messaging;

import sx.Disposable;

import java.io.File;

/**
 * Created by masc on 01.06.15.
 */
public abstract class Broker implements Disposable {
    public final static String LEO2_USERNAME = "leo2";
    public final static String LEO2_PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC";

    /**
     * Set broker data directory
     * @param file Data directory
     */
    public abstract void setDataDirectory(File file);

    /**
     * Add peer (network transport) broker
     * @param hostname
     * @param httpPort
     */
    public abstract void addPeerBroker(String hostname, Integer httpPort);

    /** Start broker */
    public abstract void start() throws Exception;
    /** Stop broker */
    public abstract void stop() throws Exception;
}
