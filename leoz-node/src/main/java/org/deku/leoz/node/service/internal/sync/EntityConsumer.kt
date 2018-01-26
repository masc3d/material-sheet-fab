package org.deku.leoz.node.service.internal.sync

import org.deku.leoz.node.data.jpa.transaction
import org.deku.leoz.node.data.repository.EntityRepository
import org.deku.leoz.node.data.jpa.truncate
import org.deku.leoz.node.service.internal.sync.EntityUpdateMessage.Companion.EOS_PROPERTY
import sx.log.slf4j.debug
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.*
import sx.mq.jms.listeners.SpringJmsListener
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.persistence.EntityManagerFactory

/**
 * Entity consumer
 * Created by masc on 18.06.15.
 * @param messagingConfiguration
 */
class EntityConsumer
(
        /** Messaging context  */
        private val requestEndpoint: JmsEndpoint,
        private val notificationEndpoint: JmsEndpoint,
        /** Entity manager factory  */
        private val entityManagerFactory: EntityManagerFactory,
        /** Executor used for listening/processing incoming messages */
        listenerExecutor: Executor)
    :
        SpringJmsListener(notificationEndpoint, listenerExecutor),
        MqHandler<EntityStateMessage> {

    /**
     * Executor for internal tasks, eg issuing requests
     */
    private var executorService: ExecutorService

    init {

        this.addDelegate(this)
        this.executorService = java.util.concurrent.Executors.newSingleThreadExecutor { r ->
            val t = Thread(r)
            t.priority = Thread.MIN_PRIORITY
            t
        }
    }

    /**
     * Entity state message handler
     */
    override fun onMessage(message: EntityStateMessage, replyChannel: MqChannel?) {
        this.request(message.entityType!!, message.syncId)
    }

    val requestClient by lazy {
        this.requestEndpoint.channel()
    }

    /**
     * Request entity update
     * @param entityType Entity type
     * @param remoteSyncId Optional remote sync-id, usually provided via notification
     * @param requestAll Ignores sync id, requests all entities (mainly for testing)
     * @param clean Clears all data before requesting
     */
    @Synchronized
    fun request(
            entityType: Class<*>,
            remoteSyncId: Long? = null,
            requestAll: Boolean = false,
            clean: Boolean = false) {

        executorService.submit<Unit> {
            // Log formatting with entity type
            val lfmt = { s: String -> "[" + entityType.canonicalName + "]" + " " + s }

            var em: javax.persistence.EntityManager? = null
            try {
                em = entityManagerFactory.createEntityManager()
                val er = EntityRepository(em, entityType)

                if (clean) {
                    em.transaction {
                        em.truncate(entityType)
                    }
                }

                var syncId: Long? = null

                if (!requestAll) {
                    syncId = er.findMaxSyncId()
                    if (syncId != null && remoteSyncId != null && remoteSyncId <= syncId) {
                        log.debug(lfmt("Entities uptodate"))
                        return@submit
                    }
                }

                if (!er.hasSyncIdAttribute()) {
                    log.debug { lfmt("No timestamp attribute found -> removing all entities") }
                    em.transaction {
                        em.truncate(entityType)
                    }
                }

                val sw = com.google.common.base.Stopwatch.createStarted()

                // Send entity state message
                this.requestClient.createReplyClient().use { replyClient ->
                    var totalCount: Long = 0
                    var bytesReceived: Long = 0

                    while (true) {
                        var count = 0
                        log.info { lfmt("Requesting with sync-id [${syncId}]") }

                        requestClient.sendRequest(
                                message = EntityStateMessage(entityType = entityType, syncId = syncId),
                                replyChannel = replyClient)

                        // Receive entity update message
                        val euMessage = replyClient.receive(
                                EntityUpdateMessage::class.java)

                        log.debug { lfmt("${euMessage}") }

                        if (euMessage.amount == 0L)
                            break

                        val emv = em!!
                        em.transaction {
                            // Receive entities
                            var eos: Boolean
                            var lastJmsTimestamp: Long = 0
                            do {
                                val tMsg = replyClient.receive()
                                        ?: throw TimeoutException("Timeout while waiting for next entities chunk")

                                // Verify message order
                                if (tMsg.jmsTimestamp < lastJmsTimestamp)
                                    throw IllegalStateException(
                                            String.format("Inconsistent message order (%d < %d)", tMsg.jmsTimestamp, syncId))

                                // Store last timestamp
                                lastJmsTimestamp = tMsg.jmsTimestamp

                                eos = tMsg.propertyExists(EOS_PROPERTY)

                                if (!eos) {
                                    // Deserialize entities
                                    val entities = replyClient.converter.fromMessage(tMsg, { size ->
                                        bytesReceived += size
                                    }) as Array<*>

                                    if (syncId != null || requestAll) {
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

                                    count += entities.size
                                }
                            } while (!eos)
                        }

                        totalCount += count

                        // For sync-id-less syncs, batches cannot be supported (TODO: remove support for this)
                        if (!er.hasSyncIdAttribute())
                            break

                        if (count < euMessage.batchSize)
                            break

                        syncId = er.findMaxSyncId()
                    }

                    log.info { lfmt("Received and stored ${totalCount} in ${sw} (${bytesReceived} bytes)") }
                }
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
    fun request(entityType: Class<*>, clean: Boolean = false) {
        this.request(
                entityType = entityType,
                remoteSyncId = null,
                clean = clean)
    }

    override fun close() {
        executorService.shutdownNow()
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            // Interruption is ok.
        }

        super.close()
    }
}
