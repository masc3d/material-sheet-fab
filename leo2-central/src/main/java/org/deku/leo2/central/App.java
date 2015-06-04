package org.deku.leo2.central;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by masc on 30.05.15.
 */
public class App extends org.deku.leo2.node.App {

    public static App instance() {
        return (App)org.deku.leo2.node.App.instance();
    }
}
