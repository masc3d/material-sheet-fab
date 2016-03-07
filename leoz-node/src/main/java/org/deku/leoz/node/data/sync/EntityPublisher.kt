package org.deku.leoz.node.data.sync

import com.google.common.base.Stopwatch
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.data.repositories.EntityRepository
import org.deku.leoz.node.data.sync.v1.EntityStateMessage
import org.deku.leoz.node.data.sync.v1.EntityUpdateMessage
import sx.Action
import sx.jms.Channel
import sx.jms.converters.DefaultConverter
import sx.jms.listeners.SpringJmsListener
import java.sql.Timestamp
import java.util.*
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * Entity publisher
 * Created by masc on 18.06.15.
 * @param messagingConfiguration
 * @param entityManagerFactory
 */
class EntityPublisher(
        /** Messaging context  */
        private val messagingConfiguration: MessagingConfiguration,
        /** Entity manager factory  */
        private val entityManagerFactory: EntityManagerFactory)
:
        SpringJmsListener( { messagingConfiguration.centralEntitySyncChannel() } )
{
    /**
     * Publish entity update notification
     * @param entityType
     * *
     * @param timestamp
     */
    @Throws(JMSException::class)
    fun publish(entityType: Class<*>, timestamp: Timestamp?) {
        ActiveMQConfiguration.instance.nodeEntitySyncBroadcastChannel().use {
            val msg = EntityStateMessage(entityType, timestamp)
            log.info("Publishing [${msg}]")
            it.send(msg)
        }
    }

    @Throws(JMSException::class)
    public override fun onMessage(message: Message, session: Session) {
        var em: EntityManager? = null
        try {
//            log.debug(String.format("Message id [%s] %s",
//                    message.jmsMessageID,
//                    LocalDateTime.ofInstant(
//                            Instant.ofEpochMilli(
//                                    message.jmsTimestamp), ZoneId.systemDefault())))

            val sw = Stopwatch.createStarted()

            // Create new message converter for this session, just for clean statistics sake
            val messageConverter = this.converter as DefaultConverter

            // Entity state message
            val esMessage = messageConverter.fromMessage(message) as EntityStateMessage
            val entityType = esMessage.entityType
            val timestamp = esMessage.timestamp
            val lfmt = { s: String -> "[" + entityType!!.canonicalName + "]" + " " + s }

            em = entityManagerFactory.createEntityManager()
            val er = EntityRepository(em, entityType)

            // Count records
            val count = er.countNewerThan(timestamp)

            val euMessage = EntityUpdateMessage(count)
            log.debug(lfmt(euMessage.toString()))

            Channel(connectionFactory = this.connectionFactory!!,
                    destination = message.jmsReplyTo,
                    converter = this.converter!!,
                    sessionTransacted = false).use {

                it.send(euMessage)

                if (count > 0) {
                    // Query with cursor
                    val cursor = er.findNewerThan(timestamp)

                    val CHUNK_SIZE = 500
                    val buffer = ArrayList<Any?>(CHUNK_SIZE)
                    log.info(lfmt("Sending ${count}"))
                    while (true) {
                        var next: Any? = null
                        if (cursor.hasNext()) {
                            next = cursor.next()
                            buffer.add(next)
                        }
                        if (buffer.size >= CHUNK_SIZE || next == null) {
                            if (buffer.size > 0) {
                                it.send(buffer.toArray())
                                buffer.clear()
                            }

                            if (next == null)
                                break
                        }
                    }

                    // Send empty array -> EOS
                    it.send(arrayOfNulls<Any>(0), messageConfigurer = Action {
                        it.setBooleanProperty(EntityUpdateMessage.EOS_PROPERTY, true)
                    })
                }
                log.info(lfmt("Sent ${count} in ${sw} (${messageConverter.bytesWritten} bytes)"))
            }
        } catch(e: Exception) {
            log.error(e.message, e);
        } finally {
            if (em != null)
                em.close()
        }
    }
}
