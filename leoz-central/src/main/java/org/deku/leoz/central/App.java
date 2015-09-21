package org.deku.leoz.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by masc on 30.05.15.
 */
public class App extends org.deku.leoz.node.App {
    private Log mLog = LogFactory.getLog(App.class);

    public static App instance() {
        return (App)org.deku.leoz.node.App.instance();
    }

    public static final String PROFILE_CENTRAL = "central";

    @Override
    public String getName() {
        return "leoz-central";
    }

    @Override
    public void initialize() {
        // No JMS logging for leoz-central
        super.initialize(PROFILE_CENTRAL);
    }
}
