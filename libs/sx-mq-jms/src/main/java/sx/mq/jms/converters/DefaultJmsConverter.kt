package sx.mq.jms.converters

import sx.io.serialization.Serializer
import sx.mq.jms.JmsConverter
import javax.jms.BytesMessage
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Object message converter
 * Created by masc on 19.06.15.
 */
class DefaultJmsConverter(
        val serializer: Serializer)
:
        JmsConverter {

    @Throws(JMSException::class)
    override fun toMessage(obj: Any, session: Session, onSize: ((size: Long) -> Unit)?): Message {
        // Create jms byte message from binary stream
        val bm = session.createBytesMessage()
        val buffer = this.serializer.serializeToByteArray(obj)
        bm.writeBytes(this.serializer.serializeToByteArray(obj))

        if (onSize != null)
            onSize(buffer.size.toLong())

        return bm
    }

    @Throws(JMSException::class)
    override fun fromMessage(message: Message, onSize: ((size: Long) -> Unit)?): Any {
        // Create binary stream from jms bytes message
        val bm = message as BytesMessage
        val size = bm.bodyLength
        val buf = ByteArray(size.toInt())
        bm.readBytes(buf)

        val obj = this.serializer.deserializeFrom(buf)

        if (onSize != null)
            onSize(size)

        return obj
    }
}
