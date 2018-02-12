package org.deku.leoz.central.service.internal.sync

import io.reactivex.subjects.PublishSubject
import sx.persistence.truncate
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.threeten.bp.Duration
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.PersistenceContext

/**
 * Database sync service
 * Created by masc on 15.05.15.
 */
@Named
open class DatabaseSyncService
@Inject
constructor(
        private val exceutorService: ScheduledExecutorService,
        @Qualifier(org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
        txJpa: PlatformTransactionManager,
        @Qualifier(org.deku.leoz.central.config.PersistenceConfiguration.QUALIFIER)
        txJooq: PlatformTransactionManager
) {

    /** Background service */
    inner class Service : sx.concurrent.Service(
            executorService = this.exceutorService,
            period = Duration.ofMinutes(1)

    ) {
        override fun run() {
            this@DatabaseSyncService.sync(false)
        }

        fun submitTask(command: () -> Unit) {
            super.submitSupplementalTask(command)
        }
    }

    /**
     * Sync presets
     */
    @Inject
    private lateinit var presets: List<Preset>

    /**
     * Embedded service class
     */
    protected open val service = Service()

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)
    }

    /** Base interface for all sync presets */
    interface Preset {}

    /**
     * Database sync preset
     * @param <TEntity> Type of destiantion JPA entity
     * @param <TCentralRecord> Type of source JOOQ record
     * @property srcJooqTable JOOQ source table
     * @property srcJooqSyncIdField JOOQ source sync id field
     * @property dstJpaEntityPath Destination QueryDSL entity table path
     * @property dstJpaSyncIdPath Destination QueryDSL sync id field path
     * @property transformation    Conversion function JOOQ record -> JPA entity
     */
    open class SimplePreset<TCentralRecord : org.jooq.Record, TEntity>(
            val srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
            val srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
            val dstJpaEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            val dstJpaSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
            val transformation: (TCentralRecord) -> TEntity
    ) : Preset {
        override fun toString(): String =
                "Preset [${srcJooqTable.name} -> ${dstJpaEntityPath.metadata.name}]"

    }

    //region Events
    data class UpdateEvent(
            val entityType: Class<out Any?>,
            val syncId: Long?
    )

    private val updatedSubject = PublishSubject.create<UpdateEvent>()
    open val updated = this.updatedSubject.hide()
    //endregion

    @PersistenceContext
    private lateinit var entityManager: javax.persistence.EntityManager

    /** JPA transaction template */
    private val transactionJpa = TransactionTemplate(txJpa).also {
        it.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    /** JOOQ transaction template */
    private val transactionJooq = TransactionTemplate(txJooq)

    // JOOQ Repositories
    @Inject
    private lateinit var syncJooqRepository: org.deku.leoz.central.data.repository.JooqSyncRepository

    @Transactional(value = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    @Synchronized
    open fun sync(clean: Boolean) {
        val sw = com.google.common.base.Stopwatch.createStarted()

        val syncIdMap = this.syncJooqRepository
                .findAll()
                .groupBy { it.tableName }
                .mapValues {
                    it.value.first().syncId
                }

        this.presets.forEach {
            try {
                when (it) {
                    is SimplePreset<*, *> ->
                        @Suppress("UNCHECKED_CAST")
                        this.update(
                                preset = it as SimplePreset<Record, Any>,
                                syncIdMap = syncIdMap,
                                deleteBeforeUpdate = clean)
                }
            } catch (e: Exception) {
                log.error("${it} failed. ${e.message}")
            }
        }

        log.info("Database sync took " + sw.toString())
    }

    /**
     * Generic updater for entites from jooq to jpa
     * @param deleteBeforeUpdate    Delete all records before updating
     */
    open fun update(preset: SimplePreset<Record, Any>,
                    syncIdMap: Map<String, Long>,
                    deleteBeforeUpdate: Boolean
    ) {
        preset.also { p ->
            // Stopwatch
            val sw = com.google.common.base.Stopwatch.createStarted()
            // Log formatter
            val lfmt = { s: String -> "[${p.dstJpaEntityPath.type.name}] ${s} $sw" }

            entityManager.flushMode = javax.persistence.FlushModeType.COMMIT

            if (deleteBeforeUpdate || p.dstJpaSyncIdPath == null) {
                transactionJpa.execute<Any> { _ ->
                    log.info(lfmt("Deleting all entities"))

                    entityManager.truncate(p.dstJpaEntityPath.type)

                    entityManager.flush()
                    entityManager.clear()
                }
            }

            // TODO. optimize by preparing query or at least caching the querydsl instance
            // Get latest timestamp
            var destMaxSyncId: Long? = null
            if (p.dstJpaSyncIdPath != null) {
                // Query embedded database table for latest timestamp
                destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<Any>(entityManager)
                        .from(p.dstJpaEntityPath)
                        .select(p.dstJpaSyncIdPath.max())
                        .fetchFirst()
            }

            // TODO. optimize by using jooq prepared statements
            if (destMaxSyncId != null) {
                val maxSyncId = syncIdMap.get(p.srcJooqTable.name)
                        ?: throw IllegalStateException("No sync id map entry for [${p.srcJooqTable.name}]")
                if (maxSyncId == destMaxSyncId) {
                    log.trace(lfmt("sync-id uptodate [${destMaxSyncId}]"))
                    return
                }
            }

            // Get newer records from central
            // masc20150530. JOOQ cursor requires an explicit transaction
            transactionJooq.execute<Any> { _ ->
                // Read source records newer than destination timestamp
                val source = syncJooqRepository.findNewerThan(
                        destMaxSyncId,
                        p.srcJooqTable,
                        p.srcJooqSyncIdField)

                if (source.hasNext()) {
                    // Save to destination/jpa
                    // REMARKS
                    // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                    log.info(lfmt("Outdated [${destMaxSyncId}]"))
                    var count = 0

                    val JPA_FLUSH_BATCH_SIZE = 100
                    val JPA_TRANSACTION_BATCH_SIZE = 10000
                    val jpaTransactionManager = transactionJpa.transactionManager
                            ?: throw IllegalStateException()

                    var transaction: TransactionStatus = jpaTransactionManager.getTransaction(transactionJpa)

                    try {
                        while (source.hasNext()) {
                            // Fetch next record
                            val record = source.fetchNext()
                            // Convert to entity
                            val entity = p.transformation(record)
                            // Store entity
                            entityManager.merge(entity)

                            count++

                            // Flush every now and then (improves performance)
                            if (count % JPA_FLUSH_BATCH_SIZE == 0) {
                                entityManager.flush()
                                entityManager.clear()
                            }

                            if (count % JPA_TRANSACTION_BATCH_SIZE == 0) {
                                jpaTransactionManager.commit(transaction)
                                transaction = jpaTransactionManager.getTransaction(transactionJpa)
                            }
                        }

                        jpaTransactionManager.commit(transaction)

                    } catch (e: Throwable) {
                        jpaTransactionManager.rollback(transaction)
                        throw e
                    }

                    // Re-query destination timestamp
                    if (p.dstJpaSyncIdPath != null) {
                        // Query embedded database for updated latest timestamp
                        destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<Any>(entityManager)
                                .from(p.dstJpaEntityPath)
                                .select(p.dstJpaSyncIdPath.max())
                                .fetchFirst()
                    }
                    log.info(lfmt("Updated ${count} entities [${destMaxSyncId}]"))

                    // Emit update event
                    this.updatedSubject.onNext(
                            UpdateEvent(
                                    entityType = p.dstJpaEntityPath.type,
                                    syncId = destMaxSyncId
                            )
                    )
                } else {
                    log.trace(lfmt("Uptodate [${destMaxSyncId}]"))
                }

                null
            }
            Unit
        }
    }

    /**
     * Sync interval
     */
    open var interval: Duration
        get() = this.service.period ?: Duration.ZERO
        set(value) {
            this.service.period = value
        }

    open fun start() {
        this.service.start()
    }

    open fun stop() {
        this.service.stop()
    }

    open fun trigger() {
        this.service.trigger()
    }

    open fun startSync(clean: Boolean) {
        this.service.submitTask {
            this@DatabaseSyncService.sync(clean)
        }
    }
}
