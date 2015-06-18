package org.deku.leo2.node;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        org.deku.leo2.node.data.PersistenceContext.class,
}, initializers = ConfigFileApplicationContextInitializer.class)
public class DataTest {
    public DataTest() {
        App.instance().initialize();

        Logger lRoot = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        lRoot.setLevel(Level.INFO);
    }

    // masc20150604. due to workaround of resteasy/spring @Context bug, have to deliver a dummy ServletContext here
    // this implies that services injecting context cannot be unit tested using this base class.
    @Bean
    ServletContext servletContext() {
        return null;
    }
}
