package org.deku.leoz.node.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.deku.leoz.node.App;
import org.slf4j.LoggerFactory;

/**
 * Created by masc on 18.06.15.
 */
public class AppTest {
    static {
        Logger lRoot = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        lRoot.setLevel(Level.INFO);

        App.getInstance().initialize();
    }

    public AppTest() {
    }
}
