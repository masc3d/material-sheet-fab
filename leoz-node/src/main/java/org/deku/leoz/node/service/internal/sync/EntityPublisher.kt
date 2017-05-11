package org.deku.leoz.node.service.internal.sync

import org.deku.leoz.node.service.internal.sync.EntityUpdateMessage.Companion.EOS_PROPERTY
import sx.mq.jms.*
import sx.mq.jms.listeners.SpringJmsListener
import javax.jms.JMSException

/**
 * Entity publisher
 * Created by masc on 18.06.15.
 * @param messagingConfiguration
 * @param entityManagerFactory
 */
class EntityPublisher(
        private val requestChannel: JmsChannel,
        private val notificationChannel: JmsChannel,
        /** Entity manager factory  */
        private val entityManagerFactory: javax.persistence.EntityManagerFactory,
        /** Executor used for listening/processing incoming messages */
        listenerExecutor: java.util.concurrent.Executor)
:
        SpringJmsListener(requestChannel, listenerExecutor),
        JmsHandler<EntityStateMessage> {
    init {
        this.addDelegate(this)
    }

    /**
     * Publish entity update notification
     * @param entityType
     * @param timestamp
     */
    @Throws(javax.jms.JMSException::class)
    fun publish(entityType: Class<*>, syncId: Long?) {
        this.notificationChannel.client().use {
            val msg = EntityStateMessage(entityType, syncId)
            log.info("Publishing [${msg}]")
            it.send(msg)
        }
    }

    @Throws(JMSException::class)
    override fun onMessage(message: EntityStateMessage, replyChannel: JmsClient?) {
        var em: javax.persistence.EntityManager? = null
        try {
            val sw = com.google.common.base.Stopwatch.createStarted()

            // Entity state message
            val esMessage = message
            val entityType = esMessage.entityType
            val syncId = esMessage.syncId
            val lfmt = { s: String -> "[" + entityType!!.canonicalName + "]" + " " + s }

            em = entityManagerFactory.createEntityManager()
            val er = org.deku.leoz.node.data.repository.EntityRepository(em, entityType!!)

            // Count records
            val count = er.countNewerThan(syncId)

            replyChannel!!.statistics.enabled = true

            // Send entity update message
            val euMessage = EntityUpdateMessage(count)
            log.debug(lfmt(euMessage.toString()))
            replyChannel.send(euMessage)

            if (count > 0) {
                // Query with cursor
                var cursor: org.eclipse.persistence.queries.ScrollableCursor? = null
                try {
                    cursor = er.findNewerThan(syncId)

                    val CHUNK_SIZE = 500
                    val buffer = java.util.ArrayList<Any?>(CHUNK_SIZE)
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
                    replyChannel.send(arrayOfNulls<Any>(0), messageConfigurer = {
                        it.setBooleanProperty(EOS_PROPERTY, true)
                    })
                } finally {
                    if (cursor != null)
                        cursor.close()
                }
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
