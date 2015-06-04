package org.deku.leo2.central;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.deku.leo2.central.data.PersistenceContext;
import org.deku.leo2.node.rest.services.v1.RoutingService;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@SpringApplicationConfiguration(classes = { PersistenceContext.class })
public class DataTest {
    static {
        App.inject(App::new);
        App.instance().initialize();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);
    }

    // masc20150604. due to workaround of resteasy/spring @Context bug, have to deliver a dummy ServletContext here
    // this implies that services injecting context cannot be unit tested using this base class.
    @Bean
    ServletContext servletContext() {
        return null;
    }
}
