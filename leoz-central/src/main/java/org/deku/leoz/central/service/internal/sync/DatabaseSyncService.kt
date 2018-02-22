package org.deku.leoz.central.service.internal.sync

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.data.repository.JooqSyncRepository
import org.deku.leoz.node.data.jpa.LclSync
import org.deku.leoz.node.data.jpa.QLclSync.lclSync
import org.deku.leoz.node.data.repository.SyncRepository
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.threeten.bp.Duration
import sx.Stopwatch
import sx.log.slf4j.debug
import sx.persistence.querydsl.from
import sx.persistence.truncate
import sx.util.toNullable
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject
import javax.persistence.PersistenceContext

/** Base interface for all sync presets */
interface Preset

/**
 * Preset for synchronizing jooq/jdbc records to jpa entities
 *
 * @param <TEntity> Type of destiantion JPA entity
 * @param <TCentralRecord> Type of source JOOQ record
 * @property srcJooqTable JOOQ source table
 * @property srcJooqSyncIdField JOOQ source sync id field
 * @property dstJpaEntityPath Destination QueryDSL entity table path
 * @property dstJpaSyncIdPath Destination QueryDSL sync id field path
 * @property transformation    Conversion function JOOQ record -> JPA entity
 */
class SyncPreset<TCentralRecord : org.jooq.Record, TEntity>(
        val srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        val srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
        val dstJpaEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
        val dstJpaSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
        val transformation: (TCentralRecord) -> TEntity
) : Preset {
    override fun toString(): String =
            "Sync preset [${srcJooqTable.name} -> ${dstJpaEntityPath.metadata.name}]"
}

/**
 * Preset for notifying about jooq/jdbc table/record changes
 * @property srcJooqTable JOOQ source table
 * @property srcJooqSyncIdField JOOQ source sync id field
 */
class NotifyPreset<TCentralRecord : org.jooq.Record>(
        val srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        val srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?
) : Preset {
    override fun toString(): String =
            "Notify preset [${srcJooqTable.name}]"
}

/**
 * Database sync service
 * Created by masc on 15.05.15.
 */
@Component
class DatabaseSyncService
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
    protected val service = Service()

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)
    }

    //region Events
    /**
     * Synchronisation update event
     * @param entityType the entity type that has been synchronized
     * @param syncId current sync id
     */
    data class UpdateEvent(
            val entityType: Class<out Any?>,
            val syncId: Long?
    )

    private val updatesSubject = PublishSubject.create<UpdateEvent>()
    val updates = this.updatesSubject.hide()

    /**
     * Notification event
     * @param tableName updated table
     * @param localSyncId Current local sync id
     **/
    data class NotificationEvent(
            val tableName: String,
            val localSyncId: Long,
            val syncId: Long? = null
    )

    private val notificationsSubject = PublishSubject.create<NotificationEvent>()

    /**
     * Observable notifications
     */
    val notifications =
            Observable.defer<NotificationEvent> {
                Observable.fromIterable(
                        this.syncRepository.findAll().map {
                            NotificationEvent(
                                    tableName = it.tableName,
                                    localSyncId = it.syncId
                            )
                        }
                )
            }
                    .concatWith(
                            this.notificationsSubject
                    )
    //endregion

    @PersistenceContext
    private lateinit var em: javax.persistence.EntityManager

    /** JPA transaction template */
    private val transactionJpa = TransactionTemplate(txJpa).also {
        it.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    // JPA repositories
    @Inject
    private lateinit var syncRepository: SyncRepository

    /** JOOQ transaction template */
    private val transactionJooq = TransactionTemplate(txJooq)

    // JOOQ Repositories
    @Inject
    private lateinit var syncJooqRepository: JooqSyncRepository

    @Suppress("UNCHECKED_CAST")
    @Transactional(value = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    @Synchronized
    fun sync(clean: Boolean) {
        val sw = Stopwatch.createStarted()

        val syncIdMap = this.syncJooqRepository
                .findAll()
                .groupBy { it.tableName }
                .mapValues {
                    it.value.first().syncId
                }

        this.presets.forEach {
            try {
                when (it) {
                    is SyncPreset<*, *> -> {
                        this.update(
                                preset = it as SyncPreset<Record, Any>,
                                syncIdMap = syncIdMap,
                                clean = clean
                        )
                    }
                    is NotifyPreset<*> -> {
                        this.update(
                                preset = it as NotifyPreset<Record>,
                                syncIdMap = syncIdMap,
                                clean = clean
                        )
                    }
                }
            } catch (e: Exception) {
                log.error("${it} failed. ${e.message}")
            }
        }

        log.debug { "Database sync took " + sw.toString() }
    }

    /**
     * Update from notify preset
     */
    fun update(preset: NotifyPreset<Record>,
                    syncIdMap: Map<String, Long>,
                    clean: Boolean) {

        val tableName = preset.srcJooqTable.name

        transactionJpa.execute<Any> { _ ->
            if (clean) {
                syncRepository.deleteAll()
            }

            val dbSyncId = syncIdMap.get(tableName)
                    ?: return@execute null

            val localSync = syncRepository.findOne(
                    lclSync.tableName.eq(tableName)
            )
                    .toNullable()
                    // Create new local sync record
                    ?: LclSync().also {
                        it.tableName = tableName
                        it.syncId = -1

                        em.persist(it)
                    }

            if (dbSyncId > localSync.syncId) {
                this.notificationsSubject.onNext(NotificationEvent(
                        tableName = tableName,
                        localSyncId = localSync.syncId,
                        syncId = dbSyncId)
                )

                // Update local sync record
                localSync.syncId = dbSyncId
                em.merge(localSync)
            }

            null
        }
    }

    /**
     * Update from sync preset
     * @param clean delete all records before updating
     */
    fun update(preset: SyncPreset<Record, Any>,
                    syncIdMap: Map<String, Long>,
                    clean: Boolean
    ) {
        val p = preset

        // Stopwatch
        val sw = Stopwatch.createStarted()
        // Log formatter
        val lfmt = { s: String -> "[${p.dstJpaEntityPath.type.name}] ${s} $sw" }

        em.flushMode = javax.persistence.FlushModeType.COMMIT

        if (clean || p.dstJpaSyncIdPath == null) {
            transactionJpa.execute<Any> { _ ->
                log.info(lfmt("Deleting all entities"))

                em.truncate(p.dstJpaEntityPath.type)

                em.flush()
                em.clear()
            }
        }

        // TODO. optimize by preparing query or at least caching the querydsl instance
        // Get latest timestamp
        var destMaxSyncId: Long? = null
        if (p.dstJpaSyncIdPath != null) {
            // Query embedded database table for latest timestamp
            destMaxSyncId = em.from(p.dstJpaEntityPath)
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
                        em.merge(entity)

                        count++

                        // Flush every now and then (improves performance)
                        if (count % JPA_FLUSH_BATCH_SIZE == 0) {
                            em.flush()
                            em.clear()
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
                    destMaxSyncId = em.from(p.dstJpaEntityPath)
                            .select(p.dstJpaSyncIdPath.max())
                            .fetchFirst()
                }
                log.info(lfmt("Updated ${count} entities [${destMaxSyncId}]"))

                // Emit update event
                this.updatesSubject.onNext(
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
    }

    /**
     * Sync interval
     */
    var interval: Duration
        get() = this.service.period ?: Duration.ZERO
        set(value) {
            this.service.period = value
        }

    fun start() {
        this.service.start()
    }

    fun stop() {
        this.service.stop()
    }

    fun trigger() {
        this.service.trigger()
    }

    fun startSync(clean: Boolean) {
        this.service.submitTask {
            this@DatabaseSyncService.sync(clean)
        }
    }
}
