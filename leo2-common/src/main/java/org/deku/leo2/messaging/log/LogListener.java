package org.deku.leo2.messaging.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Context;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import sx.Disposable;
import sx.jms.SimpleListener;
import sx.jms.SpringJmsListener;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

/**
 * Leo2 log message listener
 * Created by masc on 16.04.15.
 */
public class LogListener extends SpringJmsListener {
    private Context mContext;

    public LogListener(Context context) {
        super(context.getConnectionFactory());
        mContext = context;
    }

    @Override
    protected Destination createDestination() {
        return mContext.createQueue(LogMessage.LOG_QUEUE_NAME);
    }

    @Override
    public void onMessage(Message message) {
        this.getLog().info("message");
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) message;

                this.getLog().info("object type: " + om.getObject().getClass().getName());
                org.deku.leo2.messaging.log.v1.LogMessage lm = (org.deku.leo2.messaging.log.v1.LogMessage) om.getObject();
                this.getLog().info("object received: " + lm.toString());
            } else {
                TextMessage tm = (TextMessage) message;
                this.getLog().info("text received: " + tm.getText());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}