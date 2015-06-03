package org.deku.leo2.node;

import com.google.common.base.Stopwatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;

import java.util.logging.Logger;

/**
 * Spring boot main class.
 *
 * Created by masc on 28.05.15.
 */
@Profile(MainSpringBoot.SPRING_PROFILE_BOOT)
@Configuration("node.MainSpringBoot")
@ComponentScan
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import({
        EmbeddedServletContainerAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        ResteasyAutoConfiguration.class,
})
public class MainSpringBoot {
    /** Spring-boot profile */
    public static final String SPRING_PROFILE_BOOT = "spring-boot";

    private static Logger mLog = Logger.getLogger(MainSpringBoot.class.getName());

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        MainSpringBoot.run(MainSpringBoot.class, args);
    }

    protected static void run(Class c, String[] args) {
        Stopwatch sw = Stopwatch.createStarted();
        SpringApplication app = new SpringApplication(c);
        app.setAdditionalProfiles(SPRING_PROFILE_BOOT);
        ConfigurableApplicationContext context = app.run(args);

        mLog.info(String.format("Started in %s", sw));
    }
}
