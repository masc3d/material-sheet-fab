package org.deku.leo2.node.data.sync;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.*;

/**
 * Created by masc on 19.06.15.
 */
public class ObjectMessageConverter implements MessageConverter {
    private long mBytesWritten;
    private long mBytesRead;

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();

            BytesMessage bm = session.createBytesMessage();
            byte[] buffer = baos.toByteArray();
            bm.writeBytes(baos.toByteArray());

            mBytesWritten += buffer.length;

            return bm;
        } catch (Exception e) {
            // TODO: verify why exceptions are swallowed (apparently)
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            BytesMessage bm = (BytesMessage)message;
            int size = (int)bm.getBodyLength();
            byte[] buf = new byte[size];
            bm.readBytes(buf);

            Object object;
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            object = ois.readObject();
            ois.close();

            mBytesRead += size;

            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getBytesWritten() {
        return mBytesWritten;
    }

    public long getBytesRead() {
        return mBytesRead;
    }

    public void resetStatistics() {
        mBytesRead = mBytesWritten = 0;
    }
}
