package org.deku.leo2.central;

/**
 * leo2-central main class.
 *
 * Derives from node's main class.
 * Requires @Configuration to pull in spring components configured via base class.
 *
 * Created by masc on 30.07.14.
 */
public class Main {
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        org.deku.leo2.node.Main.run(args);
    }
}
