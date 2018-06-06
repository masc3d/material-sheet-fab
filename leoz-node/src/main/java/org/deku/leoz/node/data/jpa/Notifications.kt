package org.deku.leoz.node.data.jpa

import io.undertow.util.CopyOnWriteMap
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import sx.log.slf4j.trace

enum class JpaUpdateType {
    INSERTED,
    UPDATED,
    DELETED
}

/**
 * JPA update (event)
 */
data class JpaUpdate<T>(
        val type: JpaUpdateType,
        val entityType: Class<T>,
        val old: T?,
        val value: T
)

/**
 * JPA notifications manager
 */
@Suppress("UNCHECKED_CAST")
abstract class JpaNotifications {

    /**
     * JPA notification subscription
     */
    data class Subscription<T>(
            val entityType: Class<T>,
            val listener: (JpaUpdate<T>) -> Unit,
            val transactionManager: PlatformTransactionManager? = null
    )

    /**
     * JPA listeners
     *
     * This map is mutable as it's initialized one by one by callback.
     * Doesn't require concurrent locking though as once initialized it doesn't change.
     */
    protected val byEntityPreUpdate = mutableMapOf<Class<*>, MutableList<Subscription<Any>>>()

    /**
     * Subscribe to entity pre-updates
     *
     * @param entityType entity type
     * @param listener listener
     * @param transactionManager transaction manager for synchronisation
     */
    fun <T> subscribePreUpdate(
            entityType: Class<T>,
            listener: (JpaUpdate<T>) -> Unit,
            transactionManager: PlatformTransactionManager? = null
    ) {
        this.byEntityPreUpdate.getOrPut(entityType, {
            mutableListOf()
        }).also {
            it.add(
                    Subscription(
                            entityType = entityType as Class<Any>,
                            listener = (listener as (JpaUpdate<Any>) -> Unit),
                            transactionManager = transactionManager
                    )

            )
        }
    }
}

/**
 * JPA notification emitter
 *
 * The emitter should be fed by jpa provider specific mechanisms, eg descriptor and session listeners of eclispelink
 */
@Suppress("UNCHECKED_CAST")
open class JpaEmitter :
        JpaNotifications() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val transactionDefinition = DefaultTransactionDefinition()

    private data class TransactionState(
            val manager: PlatformTransactionManager,
            val status: TransactionStatus
    )

    // TODO: reduce locking by replacing synchonisations with copy on write (map/set/array list)

    private val activeTansactions = mutableMapOf<Long, TransactionState?>()

    fun emitTransactionBegin() {
        synchronized(this.activeTansactions) {
            this.activeTansactions.put(Thread.currentThread().id, null)
        }
    }

    fun emitTransactionCommit() {
        val threadId = Thread.currentThread().id

        synchronized(this.activeTansactions) {
            this.activeTansactions.get(threadId)
                    ?.also { taState ->
                        log.trace { "DELEGATING TRANSACTION COMMIT" }
                        taState.manager.commit(taState.status)
                    }
        }

        synchronized(this.activeTansactions) {
            this.activeTansactions.remove(threadId)
        }
    }

    /**
     * Emit pre update event
     * @param update event
     */
    fun <T> emitPreUpdate(update: JpaUpdate<T>) {
        val threadId = Thread.currentThread().id

        // Check for transaction state
        // TODO: eliminate duplicate lookup (`getOrElse` is defective with nulls)
        val hasTransaction = this.activeTansactions.contains(threadId)
        val taState = this.activeTansactions.get(threadId)

        this.byEntityPreUpdate.get(update.entityType)
                ?.forEach {
                    if (it.transactionManager != null && hasTransaction && taState == null) {
                        log.trace { "DELEGATING TRANSACTION BEGIN" }
                        val taStatus = it.transactionManager.getTransaction(transactionDefinition)

                        synchronized(this.activeTansactions) {
                            this.activeTansactions.put(threadId, TransactionState(
                                    manager = it.transactionManager,
                                    status = taStatus
                            ))
                        }
                    }

                    it.listener.invoke(update as JpaUpdate<Any>)
                }
    }
}