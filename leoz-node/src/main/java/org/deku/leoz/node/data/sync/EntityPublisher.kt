package org.deku.leoz.node.data.sync

import com.google.common.base.Stopwatch
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.data.repositories.EntityRepository
import org.deku.leoz.node.data.sync.v1.EntityStateMessage
import org.deku.leoz.node.data.sync.v1.EntityUpdateMessage
import sx.Action
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener
import java.sql.Timestamp
import java.util.*
import javax.jms.JMSException
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
        SpringJmsListener({ Channel(messagingConfiguration.entitySyncQueue) }),
        Handler<EntityStateMessage> {
    init {
        this.addDelegate(EntityStateMessage::class.java, this)
    }

    /**
     * Publish entity update notification
     * @param entityType
     * *
     * @param timestamp
     */
    @Throws(JMSException::class)
    fun publish(entityType: Class<*>, timestamp: Timestamp?) {
        Channel(ActiveMQConfiguration.instance.entitySyncTopic).use {
            val msg = EntityStateMessage(entityType, timestamp)
            log.info("Publishing [${msg}]")
            it.send(msg)
        }
    }

    @Throws(JMSException::class)
    override fun onMessage(message: EntityStateMessage, replyChannel: Channel?) {
        var em: EntityManager? = null
        try {
            val sw = Stopwatch.createStarted()

            // Entity state message
            val esMessage = message
            val entityType = esMessage.entityType
            val timestamp = esMessage.timestamp
            val lfmt = { s: String -> "[" + entityType!!.canonicalName + "]" + " " + s }

            em = entityManagerFactory.createEntityManager()
            val er = EntityRepository(em, entityType)

            // Count records
            val count = er.countNewerThan(timestamp)

            replyChannel!!.statistics.enabled = true

            // Send entity update message
            val euMessage = EntityUpdateMessage(count)
            log.debug(lfmt(euMessage.toString()))
            replyChannel.send(euMessage)

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
                            replyChannel.send(buffer.toArray())
                            buffer.clear()
                        }

                        if (next == null)
                            break
                    }
                }

                // Send empty array -> EOS
                replyChannel.send(arrayOfNulls<Any>(0), messageConfigurer = Action {
                    it.setBooleanProperty(EntityUpdateMessage.EOS_PROPERTY, true)
                })
            }
            log.info(lfmt("Sent ${count} in ${sw} (${replyChannel.statistics.bytesSent} bytes)"))

        } catch(e: Exception) {
            log.error(e.message, e);
        } finally {
            if (em != null)
                em.close()
        }
    }
}
