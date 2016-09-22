package org.deku.leoz.node.data.sync

import com.google.common.base.Stopwatch
import org.deku.leoz.node.data.PersistenceUtil
import org.deku.leoz.node.data.repositories.EntityRepository
import org.deku.leoz.node.messaging.entities.EntityStateMessage
import org.deku.leoz.node.messaging.entities.EntityUpdateMessage
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener
import java.sql.Timestamp
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * Entity consumer
 * Created by masc on 18.06.15.
 * @param messagingConfiguration
 */
class EntityConsumer
(
        /** Messaging context  */
        private val requestChannelConfiguration: Channel.Configuration,
        private val notificationChannelConfiguration: Channel.Configuration,
        /** Entity manager factory  */
        private val entityManagerFactory: EntityManagerFactory,
        executor: Executor)
:
        SpringJmsListener({ Channel(notificationChannelConfiguration) }, executor),
        Handler<EntityStateMessage> {

    private var executorService: ExecutorService

    init {

        this.addDelegate(this)
        executorService = Executors.newSingleThreadExecutor { r ->
            val t = Thread(r)
            t.priority = Thread.MIN_PRIORITY
            t
        }
    }

    /**
     * Entity state message handler
     */
    override fun onMessage(message: EntityStateMessage, replyChannel: Channel?) {
        this.request(message.entityType!!, message.syncId)
    }

    val requestChannel by lazy {
        Channel(this.requestChannelConfiguration)
    }

    val replyChannel by lazy {
        requestChannel.createReplyChannel()
    }

    /**
     * Request entity update
     * @param entityType Entity type
     * @param remoteSyncId Optional remote timestamp, usually provided via notification
     * @param requestAll Ignores sync id, requests all entities (mainly for testing)
     */
    @Synchronized fun request(entityType: Class<*>, remoteSyncId: Long?, requestAll: Boolean = false) {
        executorService.submit<Unit> {
            // Log formatting with entity type
            val lfmt = { s: String -> "[" + entityType.canonicalName + "]" + " " + s }

            var em: EntityManager? = null
            try {
                em = entityManagerFactory.createEntityManager()
                val er = EntityRepository(em, entityType)

                var syncId: Long? = null

                if (!requestAll) {
                    syncId = er.findMaxSyncId()
                    if (syncId != null && remoteSyncId != null && remoteSyncId <= syncId) {
                        log.debug(lfmt("Entities uptodate"))
                        return@submit
                    }
                }

                val sw = Stopwatch.createStarted()

                // Send entity state message
                requestChannel.sendRequest(EntityStateMessage(entityType, syncId), replyChannel = this.replyChannel)

                log.info(lfmt("Requesting entities"))

                // Receive entity update message
                val euMessage = replyChannel.receive(EntityUpdateMessage::class.java)

                log.debug(lfmt(euMessage.toString()))
                val count = AtomicLong()

                var bytesReceived = 0L
                if (euMessage.amount > 0) {
                    val emv = em!!
                    PersistenceUtil.transaction(em) {
                        if (!er.hasSyncIdAttribute()) {
                            log.debug(lfmt("No timestamp attribute found -> removing all entities"))
                            er.removeAll()
                        }

                        // Receive entities
                        var eos: Boolean
                        var lastJmsTimestamp: Long = 0
                        do {
                            val tMsg = replyChannel.receive() ?: throw TimeoutException("Timeout while waiting for next entities chunk")

                            // Verify message order
                            if (tMsg.jmsTimestamp < lastJmsTimestamp)
                                throw IllegalStateException(
                                        String.format("Inconsistent message order (%d < %d)", tMsg.jmsTimestamp, syncId))

                            // Store last timestamp
                            lastJmsTimestamp = tMsg.jmsTimestamp

                            eos = tMsg.propertyExists(EntityUpdateMessage.EOS_PROPERTY)

                            if (!eos) {
                                // Deserialize entities
                                val entities = replyChannel.converter.fromMessage(tMsg, { size ->
                                    bytesReceived += size
                                }) as Array<*>

                                // TODO: exceptions within transactions behave in a strange way.
                                // data of transactions that were committed may not be there and h2 may report
                                // cache level state nio exceptions.
                                // Data seems to remain consistent though
                                if (syncId != null || requestAll) {
                                    log.trace(lfmt("Removing existing entities"))
                                    // If there's already entities, clean out existing first.
                                    // it's much faster than merging everything
                                    for (o in entities) {
                                        val o2 = emv.find(entityType,
                                                emv.entityManagerFactory.persistenceUnitUtil.getIdentifier(o))

                                        if (o2 != null) {
                                            emv.remove(o2)
                                        }
                                    }
                                    emv.flush()
                                    emv.clear()
                                }

                                // Persist entities
                                for (o in entities) {
                                    emv.persist(o)
                                }
                                emv.flush()
                                emv.clear()
                                count.getAndUpdate { c -> c + entities.size }
                            }
                        } while (!eos)
                    }

                }
                log.info(lfmt("Received and stored ${count.get()} in ${sw} (${bytesReceived} bytes)"))
            } catch (e: TimeoutException) {
                log.error(lfmt(e.message ?: ""))
            } catch (e: Throwable) {
                log.error(lfmt(e.message ?: ""), e)
            } finally {
                if (em != null)
                    em.close()
            }
        }
    }

    /**
     * Request entity update
     * @param entityType Entity type
     */
    fun request(entityType: Class<*>) {
        this.request(entityType, null)
    }

    override fun close() {
        executorService.shutdownNow()
        try {
            executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            // Interruption is ok.
        }

        super.close()
    }
}
