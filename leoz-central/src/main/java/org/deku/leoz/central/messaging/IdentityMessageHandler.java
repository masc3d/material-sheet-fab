package org.deku.leoz.central.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.central.data.entities.jooq.tables.records.MstNodeRecord;
import org.deku.leoz.central.data.repositories.NodeRepository;
import org.deku.leoz.node.messaging.auth.v1.AuthorizationMessage;
import org.deku.leoz.node.messaging.auth.v1.IdentityMessage;
import sx.jms.Handler;
import sx.jms.Converter;
import sx.jms.converters.DefaultConverter;

import javax.jms.*;

/**
 * Created by masc on 01.07.15.
 */
public class IdentityMessageHandler implements Handler<IdentityMessage> {
    private Log mLog = LogFactory.getLog(this.getClass());

    private NodeRepository mNodeRepository;
    private Converter mConverter;

    public IdentityMessageHandler(NodeRepository nodeRepository) {
        mNodeRepository = nodeRepository;

        mConverter = new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP);
    }

    @Override
    public void onMessage(IdentityMessage message, Message jmsMessage, Session session) throws JMSException {
        try {
            mLog.info(message);

            MstNodeRecord r = mNodeRepository.findByKeyOrCreateNew(message.getKey());

            r.setHostname(message.getHardwareAddress());
            r.setKey(message.getKey());
            r.setSysInfo(message.getSystemInfo());
            r.store();

            Destination replyTo = jmsMessage.getJMSReplyTo();
            if (replyTo != null) {
                AuthorizationMessage am = new AuthorizationMessage();
                am.setId(r.getNodeId());
                am.setKey(r.getKey());
                am.setAuthorized(r.getAuthorized() != null && r.getAuthorized() != 0);

                MessageProducer mp = session.createProducer(replyTo);
                mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                mp.setTimeToLive(10 * 1000);
                mp.setPriority(8);
                mp.send(mConverter.toMessage(am, session));

                session.commit();

                mLog.info(String.format("Sent authorization [%s]", am));
            }
        } catch(Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
