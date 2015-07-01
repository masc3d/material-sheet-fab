package org.deku.leo2.central.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.messaging.auth.v1.IdentityMessage;
import sx.jms.Handler;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by masc on 01.07.15.
 */
public class IdentityMessageHandler implements Handler<IdentityMessage> {
    private Log mLog = LogFactory.getLog(this.getClass());

    @Override
    public void onMessage(IdentityMessage message, Session session) throws JMSException {
        mLog.info(message);
    }
}
