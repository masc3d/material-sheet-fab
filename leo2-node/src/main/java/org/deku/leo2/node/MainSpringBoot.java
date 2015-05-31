package org.deku.leo2.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 *
 * Created by masc on 28.05.15.
 */
@Profile(MainSpringBoot.SPRING_PROFILE_BOOT)
@Configuration("node.MainSpringBoot")
@ComponentScan
@EnableWebMvc
@Import({
        EmbeddedServletContainerAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        ResteasyAutoConfiguration.class,
})
public class MainSpringBoot {
    /** Profile name for spring-boot */
    public static final String SPRING_PROFILE_BOOT = "spring-boot";

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        MainSpringBoot.run(MainSpringBoot.class, args);
    }

    protected static void run(Class c, String[] args) {
        Global.instance().initializeLogging();

        SpringApplication app = new SpringApplication(c);
        app.setAdditionalProfiles(SPRING_PROFILE_BOOT);
        ConfigurableApplicationContext context = app.run(args);
    }
}
