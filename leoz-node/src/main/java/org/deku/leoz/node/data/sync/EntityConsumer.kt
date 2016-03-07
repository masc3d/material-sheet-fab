package org.deku.leoz.node.data.sync

import com.google.common.base.Stopwatch
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.data.PersistenceUtil
import org.deku.leoz.node.data.repositories.EntityRepository
import org.deku.leoz.node.data.sync.v1.EntityStateMessage
import org.deku.leoz.node.data.sync.v1.EntityUpdateMessage
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import sx.jms.listeners.SpringJmsListener
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session
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
        private val messagingConfiguration: MessagingConfiguration,
        /** Entity manager factory  */
        private val entityManagerFactory: EntityManagerFactory)
:
        SpringJmsListener({ Channel(messagingConfiguration.entitySyncTopic) }),
        Handler<EntityStateMessage> {
    private var executorService: ExecutorService

    init {

        this.addDelegate(EntityStateMessage::class.java, this)
        executorService = Executors.newSingleThreadExecutor { r ->
            val t = Thread(r)
            t.priority = Thread.MIN_PRIORITY
            t
        }
    }

    /**
     * Entity state message handler
     */
    override fun onMessage(message: EntityStateMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        this.request(message.entityType!!, message.timestamp)
    }

    /**
     * Request entity update
     * @param entityType Entity type
     * @param remoteTimestamp Optional remote timestamp, usually provided via notification
     */
    @Synchronized fun request(entityType: Class<*>, remoteTimestamp: Timestamp?) {
        executorService.submit<Unit> {
            // Log formatting with entity type
            val lfmt = { s: String -> "[" + entityType.canonicalName + "]" + " " + s }

            var em: EntityManager? = null
            try {
                val converter = this.converter as DefaultConverter
                converter.resetStatistics()

                em = entityManagerFactory.createEntityManager()
                val er = EntityRepository(em, entityType)

                val timestamp = er.findMaxTimestamp()
                if (timestamp != null && remoteTimestamp != null && !remoteTimestamp.after(timestamp)) {
                    log.debug(lfmt("Entities uptodate"))
                    return@submit
                }

                Channel(this.messagingConfiguration.entitySyncQueue).use { entitySyncChannel ->
                    val sw = Stopwatch.createStarted()

                    // Send entity state message
                    entitySyncChannel.sendRequest(EntityStateMessage(entityType, timestamp)).use { replyChannel ->
                        log.info(lfmt("Requesting entities"))

                        // Receive entity update message
                        val euMessage = replyChannel.receive(EntityUpdateMessage::class.java)

                        log.debug(lfmt(euMessage.toString()))
                        val count = AtomicLong()

                        // Transaction processing thread pool
                        val executorService = Executors.newFixedThreadPool(
                                Runtime.getRuntime().availableProcessors())

                        if (euMessage.amount > 0) {
                            val emv = em!!
                            PersistenceUtil.transaction(em) {
                                if (!er.hasTimestampAttribute()) {
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
                                                String.format("Inconsistent message order (%d < %d)", tMsg.jmsTimestamp, timestamp))

                                    // Store last timestamp
                                    lastJmsTimestamp = tMsg.jmsTimestamp

                                    eos = tMsg.propertyExists(EntityUpdateMessage.EOS_PROPERTY)

                                    if (!eos) {
                                        // Deserialize entities
                                        val entities = Arrays.asList(*converter.fromMessage(tMsg) as Array<*>)

                                        // TODO: exceptions within transactions behave in a strange way.
                                        // data of transactions that were committed may not be there and h2 may report
                                        // cache level state nio exceptions.
                                        // Data seems to remain consistent though

                                        if (timestamp != null) {
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
                        log.trace(lfmt("Joining transaction threads"))
                        executorService.shutdown()
                        executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
                        log.info(lfmt("Received and stored ${count.get()} in ${sw} (${converter.bytesRead} bytes)"))
                    }
                }
            } catch (e: TimeoutException) {
                log.error(lfmt(e.message ?: ""))
            } catch (e: Exception) {
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
        executorService.shutdown()
        try {
            executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            // Interruption is ok.
        }

        super.close()
    }
}
