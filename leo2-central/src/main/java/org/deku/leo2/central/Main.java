package org.deku.leo2.central;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@ComponentScan
public class Main extends org.deku.leo2.node.Main {
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        org.deku.leo2.node.Main.run(Main.class, args);
    }
}