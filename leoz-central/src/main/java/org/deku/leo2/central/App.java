package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by masc on 30.05.15.
 */
public class App extends org.deku.leo2.node.App {
    private Log mLog = LogFactory.getLog(App.class);

    public static App instance() {
        return (App)org.deku.leo2.node.App.instance();
    }

    public static final String PROFILE_CENTRAL = "central";

    @Override
    public void initialize() {
        // No JMS logging for leo2-central
        super.initialize(PROFILE_CENTRAL);
    }
}
