package org.deku.leo2.node;

import com.google.common.base.Stopwatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.util.logging.Logger;

/**
 * Spring boot main class.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("node.MainSpringBoot")
@ComponentScan
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import({
        EmbeddedServletContainerAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        ResteasyAutoConfiguration.class,
})
public class Main {
    private static Logger mLog = Logger.getLogger(Main.class.getName());

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        Main.run(Main.class, args);
    }

    protected static void run(Class c, String[] args) {
        Stopwatch sw = Stopwatch.createStarted();
        SpringApplication app = new SpringApplication(c);
        ConfigurableApplicationContext context = app.run(args);

        mLog.info(String.format("Started in %s", sw));
    }
}
