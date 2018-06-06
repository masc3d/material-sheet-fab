package org.deku.leoz.node.service.internal.sync

import org.deku.leoz.node.data.repository.SyncRepository
import org.deku.leoz.node.service.internal.sync.EntityUpdateMessage.Companion.EOS_PROPERTY
import org.slf4j.event.Level
import sx.Stopwatch
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.JmsChannel
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.channel
import sx.mq.jms.listeners.SpringJmsListener
import javax.jms.JMSException

/**
 * Entity publisher
 * Created by masc on 18.06.15.
 * @param messagingConfiguration
 * @param entityManagerFactory
 */
class EntityPublisher(
        private val requestEndpoint: JmsEndpoint,
        private val notificationEndpoint: JmsEndpoint,

        /** Entity manager factory  */
        private val entityManagerFactory: javax.persistence.EntityManagerFactory,

        /** Sync repository */
        private val syncRepository: SyncRepository,

        /** Executor used for listening/processing incoming messages */
        listenerExecutor: java.util.concurrent.Executor,

        val presets: List<PublisherPreset<*>> = listOf()
) :
        SpringJmsListener(requestEndpoint, listenerExecutor),
        MqHandler<EntityStateMessage> {

    /** Publisher presets by tyoe */
    private val presetsByType: Map<Class<*>, PublisherPreset<*>>

    init {
        this.addDelegate(this)

        this.presetsByType = presets.associateBy { it.entityPath.type }
    }

    /**
     * Publish entity update notification
     * @param entityType
     * @param timestamp
     */
    @Throws(javax.jms.JMSException::class)
    fun publish(entityType: Class<*>, syncId: Long?) {
        this.notificationEndpoint.channel().use {
            val msg = EntityStateMessage(entityType, syncId)
            log.info("Publishing [${msg}]")
            it.send(msg)
        }
    }

    @Throws(JMSException::class)
    override fun onMessage(message: EntityStateMessage, replyChannel: MqChannel?) {
        var em: javax.persistence.EntityManager? = null
        try {
            val sw = Stopwatch.createStarted()

            // Entity state message
            val state = message
            val entityType = state.entityType
            val syncId = state.syncId

            fun lfmt(msg: String) = "[${entityType!!.canonicalName}] ${msg}"

            val preset = presetsByType.get(entityType)

            if (preset == null) {
                log.warn("No preset for entity type [${entityType}]")
                return
            }

            em = entityManagerFactory.createEntityManager()

            val BATCH_SIZE = 100000
            val CHUNK_SIZE = 500

            log.info { lfmt("Remote has [${message.syncId}]") }

            // Count records
            val count = Stopwatch.createStarted(this, lfmt("COUNT NEWER"), Level.DEBUG, {
                syncRepository.countNewerThan(
                        preset.entityPath,
                        preset.syncIdPath,
                        syncId = syncId
                )
            })

            if (replyChannel == null || !(replyChannel is JmsChannel))
                throw IllegalArgumentException("IMS reply client required")

            replyChannel.statistics.enabled = true

            // Send entity update message
            val euMessage = EntityUpdateMessage(
                    amount = count,
                    batchSize = BATCH_SIZE)

            log.info { lfmt("Sending [${euMessage}]") }

            replyChannel.send(euMessage)

            if (count > 0) {
                // Query with cursor
                var cursor: org.eclipse.persistence.queries.CursoredStream? = null
                try {
                    var sentCount = 0
                    val buffer = java.util.ArrayList<Any?>(CHUNK_SIZE)

                    cursor = Stopwatch.createStarted(this, lfmt("Find newer"), Level.DEBUG, {
                        syncRepository.findNewerThan(
                                preset.entityPath,
                                preset.syncIdPath,
                                syncId = syncId,
                                maxResults = BATCH_SIZE
                        )
                    })

                    while (sentCount < BATCH_SIZE) {
                        var next: Any? = null

                        if (cursor.hasNext()) {
                            next = cursor.next()
                            buffer.add(next)
                        }

                        if (buffer.size >= CHUNK_SIZE || next == null) {
                            if (buffer.size > 0) {
                                replyChannel.send(buffer.toArray())
                                sentCount += buffer.size
                                buffer.clear()
                            }

                            if (next == null)
                                break
                        }
                    }

                    // Send empty array -> EOS
                    replyChannel.send(arrayOfNulls<Any>(0), also = {
                        it.setBooleanProperty(EOS_PROPERTY, true)
                    })
                } finally {
                    if (cursor != null)
                        cursor.close()
                }
            }
            log.info(lfmt("Sent ${count} in ${sw} (${replyChannel.statistics.bytesSent} bytes)"))
        } catch (e: Exception) {
            log.error(e.message, e);
        } finally {
            if (em != null)
                em.close()
        }
    }
}
