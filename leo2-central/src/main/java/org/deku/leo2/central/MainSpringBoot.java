package org.deku.leo2.central;

import org.deku.leo2.node.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import javax.inject.Named;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 *
 * Created by masc on 28.05.15.
 */
@Profile(MainSpringBoot.SPRING_PROFILE_BOOT)
@Configuration("central.MainSpringBoot")
@ComponentScan
public class MainSpringBoot extends org.deku.leo2.node.MainSpringBoot {
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        org.deku.leo2.node.MainSpringBoot.run(MainSpringBoot.class, args);
    }
}