package org.deku.leo2.central.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.entities.jooq.tables.records.MstNodeRecord;
import org.deku.leo2.central.data.repositories.jooq.NodeJooqRepository;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import org.deku.leo2.node.messaging.auth.v1.IdentityMessage;
import sx.jms.Handler;
import sx.jms.MessageConverter;
import sx.jms.converters.DefaultMessageConverter;

import javax.jms.*;

/**
 * Created by masc on 01.07.15.
 */
public class IdentityMessageHandler implements Handler<IdentityMessage> {
    private Log mLog = LogFactory.getLog(this.getClass());

    private NodeJooqRepository mNodeJooqRepository;
    private MessageConverter mMessageConverter;

    public IdentityMessageHandler(NodeJooqRepository nodeJooqRepository) {
        mNodeJooqRepository = nodeJooqRepository;

        mMessageConverter = new DefaultMessageConverter(
                DefaultMessageConverter.SerializationType.KRYO,
                DefaultMessageConverter.CompressionType.GZIP);
    }

    @Override
    public void onMessage(IdentityMessage message, Message jmsMessage, Session session) throws JMSException {
        try {
            mLog.info(message);

            MstNodeRecord r = mNodeJooqRepository.saveByKey(message.getKey(),
                    message.getHardwareAddress(),
                    message.getSystemInfo());

            Destination replyTo = jmsMessage.getJMSReplyTo();
            if (replyTo != null) {
                AuthorizationMessage am = new AuthorizationMessage();
                am.setId(r.getNodeId());
                am.setKey(r.getKey());
                am.setAuthorized(r.getAuthorized() != null && r.getAuthorized() != 0);

                MessageProducer mp = session.createProducer(replyTo);
                mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                mp.setTimeToLive(10 * 1000);
                mp.send(mMessageConverter.toMessage(am, session));

                session.commit();
            }
        } catch(Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
