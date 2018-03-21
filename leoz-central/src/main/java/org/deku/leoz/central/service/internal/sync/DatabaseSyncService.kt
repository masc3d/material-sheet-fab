package org.deku.leoz.central.service.internal.sync

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.data.jooq.dekuclient.tables.SysSync
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SysSyncRecord
import org.deku.leoz.central.data.repository.JooqSyncRepository
import org.deku.leoz.node.data.jpa.LclSync
import org.deku.leoz.node.data.jpa.QLclSync.lclSync
import org.deku.leoz.node.data.repository.SyncRepository
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.threeten.bp.Duration
import sun.rmi.transport.tcp.TCPEndpoint
import sx.Stopwatch
import sx.log.slf4j.debug
import sx.log.slf4j.trace
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.truncate
import sx.rx.subscribeOn
import sx.rx.toHotCache
import sx.util.toNullable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.properties.Delegates

/**
 * Base class for all sync presets
 * @param <TCentralRecord> Type of source JOOQ record
 **/
abstract class Preset<TCentralRecord : org.jooq.Record>(
        /** JOOQ source table */
        val srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        /** JOOQ source sync id field */
        val srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
        /** Synchronization interval */
        val interval: Duration
) {
    val tableName by lazy {
        this.srcJooqTable.name
    }
}

/**
 * Preset for notifying about jooq/jdbc table/record changes
 */
class NotifyPreset<TCentralRecord : org.jooq.Record>(
        srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
        interval: Duration = Duration.ofSeconds(5)
) : Preset<TCentralRecord>(
        srcJooqTable = srcJooqTable,
        srcJooqSyncIdField = srcJooqSyncIdField,
        interval = interval
) {
    override fun toString(): String =
            "Notify preset [${srcJooqTable.name}]"
}

/**
 * Preset for synchronizing jooq/jdbc records to jpa entities
 *
 * @param <TEntity> Type of destiantion JPA entity
 * @param <TCentralRecord> Type of source JOOQ record
 */
class SyncPreset<TCentralRecord : org.jooq.Record, TEntity>(
        srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
        /** Destination QueryDSL entity table path */
        val dstJpaEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
        /** Destination QueryDSL sync id field path */
        val dstJpaSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
        /** Conversion function JOOQ record -> JPA entity */
        val transformation: (TCentralRecord) -> TEntity,
        interval: Duration = Duration.ofMinutes(1)
) : Preset<TCentralRecord>(
        srcJooqTable = srcJooqTable,
        srcJooqSyncIdField = srcJooqSyncIdField,
        interval = interval
) {
    override fun toString(): String =
            "Sync preset [${srcJooqTable.name} -> ${dstJpaEntityPath.metadata.name}]"
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
        txJpa: JpaTransactionManager,
        @Qualifier(org.deku.leoz.central.config.PersistenceConfiguration.QUALIFIER)
        txJooq: PlatformTransactionManager
) {
    private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)

    /**
     * Sync presets
     */
    @Inject
    private lateinit var presets: List<Preset<*>>

    // JPA repositories
    @Inject
    private lateinit var syncRepository: SyncRepository

    // JOOQ Repositories
    @Inject
    private lateinit var syncJooqRepository: JooqSyncRepository

    /** JOOQ transaction template */
    private val transactionJooq = TransactionTemplate(txJooq)

    @PersistenceContext
    private lateinit var em: EntityManager

    /** JPA transaction template */
    private val transactionJpa = TransactionTemplate(txJpa).also {
        it.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    /**
     * Sync interval
     */
    var interval: Duration = Duration.ofSeconds(5)

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
                // Emit local sync ids initially (deferred so those are emitter on each subscription)
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

    /**
     * Sync process step
     */
    private class ProcessStep {
        /** Source (central) sync record */
        var srcSyncRecord: SysSyncRecord by Delegates.notNull()
        /** Preset to process */
        var preset: Preset<*> by Delegates.notNull()
    }

    /** Reactive sync processing */
    private val process
        get() =
            Observable
                    // Emit trigger
                    .interval(this@DatabaseSyncService.interval.toMillis(), TimeUnit.MILLISECONDS)
                    .flatMap {
                        Observable.fromIterable(
                                // Retrieve source (central) sync ids and map to process steps
                                this.syncJooqRepository
                                        .findAll()
                                        .mapNotNull { record ->
                                            val preset = this.presets.firstOrNull { it.tableName == record.tableName }

                                            // Ignore source sync entries which have no referring preset
                                            if (preset != null)
                                                ProcessStep().also {
                                                    it.srcSyncRecord = record
                                                    it.preset = preset
                                                }
                                            else
                                                null
                                        }
                        )
                    }
                    // Group process steps by table name in order to throttle by table
                    .groupBy { it.srcSyncRecord.tableName }
                    .flatMap { tableSteps ->
                        val tableName = tableSteps.key
                        val preset = this.presets.first { it.tableName == tableName }

                        // Throttle accordingly
                        tableSteps.throttleLast(preset.interval.toMillis(), TimeUnit.MILLISECONDS)
                    }
                    .doOnNext { step ->
                        val sw = Stopwatch.createStarted()

                        // Actual sync preset processing
                        try {
                            val updated = step.preset.update(
                                    sysSyncRecord = step.srcSyncRecord,
                                    clean = false
                            )

                            val message = { "${step.preset.javaClass.simpleName} [${step.preset.tableName}] took ${sw}" }

                            if (updated)
                                log.debug(message)
                            else
                                log.trace(message)
                        } catch (e: Throwable) {
                            log.error(e.message, e)
                        }
                    }
                    .subscribeOn(this.exceutorService)

    /** Reactive sync process subscription */
    private var processSubscription: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
            if (field != null) {
                // Perform initial sync
                this.sync(clean = false)
            }
        }

    /**
     * Run all synchronisation presets (asynchronously)
     * @param clean Perform clean for all presets
     * @return Hot completable for this operation
     */
    fun sync(clean: Boolean): Completable {
        return this.exceutorService.submit {
            val sw = Stopwatch.createStarted()

            val syncMap = this.syncJooqRepository
                    .findAll()
                    .groupBy { it.tableName }
                    .mapValues {
                        it.value.first()
                    }

            this.presets.forEach {
                try {
                    it.update(
                            sysSyncRecord = syncMap.getValue(it.tableName),
                            clean = clean
                    )
                } catch (e: Exception) {
                    log.error("${it} failed. ${e.message}")
                }
            }

            log.debug { "Database sync took " + sw.toString() }
        }
                .toCompletable()
    }

    /**
     * Preliminary delete support
     */
    private fun <TCentralRecord : Record, TEntity> delete(
            srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
            srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>,
            /** Destination QueryDSL entity table path */
            dstJpaEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            /** Destination QueryDSL sync id field path */
            dstJpaSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>,
            destMaxSyncId: Long
            ) {

        // TODO: currently only for testing performance of traversing sync ids
        Stopwatch.createStarted(this, "TRAVERSE SYNCIDS", {
            transactionJpa.execute<Any> { _ ->
                syncJooqRepository.findSyncIdsNewerThan(
                        syncId = 0,
                        table = srcJooqTable,
                        syncIdField = srcJooqSyncIdField
                )
                        .forEach {
                            // TODO: delete
                        }
            }
        })
    }


    /**
     * Abstract update extension
     *
     * NOTE: this method must be @Synchronized to prevent concurrent updates of the same preset / table
     */
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    private fun Preset<*>.update(
            sysSyncRecord: SysSyncRecord,
            clean: Boolean): Boolean {

        return when (this) {
            is SyncPreset<*, *> -> (this as SyncPreset<Record, Any>).update(
                    sysSyncRecord = sysSyncRecord,
                    clean = clean
            )
            is NotifyPreset<*> -> (this as NotifyPreset<Record>).update(
                    sysSyncRecord = sysSyncRecord,
                    clean = clean
            )
            else -> false
        }
    }

    /**
     * Update from notify preset
     */
    private fun NotifyPreset<Record>.update(
            sysSyncRecord: SysSyncRecord,
            clean: Boolean): Boolean {

        val tableName = this.srcJooqTable.name

        return transactionJpa.execute { _ ->
            if (clean) {
                syncRepository.deleteAll()
            }

            val dbSyncId = sysSyncRecord.syncId

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
                notificationsSubject.onNext(DatabaseSyncService.NotificationEvent(
                        tableName = tableName,
                        localSyncId = localSync.syncId,
                        syncId = dbSyncId)
                )

                // Update local sync record
                localSync.syncId = dbSyncId
                em.merge(localSync)

                true
            } else {
                false
            }
        }!!
    }

    /**
     * Update from sync preset
     * @param clean delete all records before updating
     */
    private fun SyncPreset<Record, Any>.update(
            sysSyncRecord: SysSyncRecord,
            clean: Boolean
    ): Boolean {
        val p = this

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
            if (sysSyncRecord.syncId == destMaxSyncId) {
                log.trace(lfmt("sync-id uptodate [${destMaxSyncId}]"))
                return false
            }

            if (dstJpaSyncIdPath != null && srcJooqSyncIdField != null) {
                //region TODO: delete. IMPORTANT: only for testing for the moment
//                this@DatabaseSyncService.delete(
//                        srcJooqTable = srcJooqTable,
//                        dstJpaEntityPath = dstJpaEntityPath,
//                        dstJpaSyncIdPath = dstJpaSyncIdPath,
//                        srcJooqSyncIdField = srcJooqSyncIdField,
//                        destMaxSyncId = destMaxSyncId
//                )
                //endregion
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
                updatesSubject.onNext(
                        DatabaseSyncService.UpdateEvent(
                                entityType = p.dstJpaEntityPath.type,
                                syncId = destMaxSyncId
                        )
                )
            } else {
                log.trace(lfmt("Uptodate [${destMaxSyncId}]"))
            }

            null
        }

        return true
    }

    fun start() {
        this.processSubscription = process.subscribe()
    }

    fun stop() {
        // TODO graceful shutdown. currently start/stop is not synchronized
        this.processSubscription = null
    }
}
