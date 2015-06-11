package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMqBroker;
import org.deku.leo2.messaging.activemq.ActiveMqContext;
import org.deku.leo2.messaging.log.LogListener;

import javax.jms.JMSException;

/**
 * Created by masc on 30.05.15.
 */
public class App extends org.deku.leo2.node.App {
    private Log mLog = LogFactory.getLog(App.class);

    LogListener mLogListener;

    private ActiveMqBroker.Listener mBrokerListener = new ActiveMqBroker.Listener() {
        @Override
        public void onStart() {
            mLog.info("Detected broker start, attaching listeners");
            mLogListener = new LogListener(ActiveMqContext.instance());
            try {
                mLogListener.start();
            } catch (JMSException e) {
                mLog.error(e.getMessage(), e);
            }
        }
    };

    public static App instance() {
        return (App)org.deku.leo2.node.App.instance();
    }


    @Override
    public void initialize() throws Exception {
        super.initialize();

        // Register to broker start
        ActiveMqBroker.instance().getListenerEventDispatcher().add(mBrokerListener);
    }
}
