package org.deku.leoz.central.service.internal.sync

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Path
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.isJooqAccessException
import org.deku.leoz.central.data.repository.JooqSyncRepository
import org.deku.leoz.node.data.jpa.JpaNotifications
import org.deku.leoz.node.data.jpa.JpaUpdateType
import org.deku.leoz.node.data.jpa.LclSync
import org.deku.leoz.node.data.jpa.QLclSync.lclSync
import org.deku.leoz.node.data.repository.SyncRepository
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.UpdatableRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.threeten.bp.Duration
import sx.Stopwatch
import sx.log.slf4j.debug
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.log.slf4j.warn
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.truncate
import sx.reflect.PropertyAccess
import sx.rx.retryWith
import sx.rx.subscribeOn
import sx.util.toByteArray
import sx.util.toNullable
import sx.util.toUUID
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.FlushModeType
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
        val srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>? = null,
        /** Synchronization interval */
        val interval: Duration? = null
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
            "Notify [${srcJooqTable.name}]"
}

/**
 * Extension for transforming sync preset to repository sync spec
 */
fun List<Preset<*>>.toSyncSpec(): List<JooqSyncRepository.SyncSpec<*>> {
    return this.mapNotNull {
        if (it.srcJooqSyncIdField != null)
            JooqSyncRepository.SyncSpec(it.srcJooqTable, it.srcJooqSyncIdField)
        else
            null
    }
}

/**
 * Preset for synchronizing jooq/jdbc records to jpa entities
 *
 * @param <TEntity> Type of destiantion JPA entity
 * @param <TCentralRecord> Type of source JOOQ record
 */
@Suppress("UNCHECKED_CAST")
class SyncPreset<TCentralRecord : org.jooq.Record, TEntity>(
        /** Source jooq table */
        srcJooqTable: org.jooq.impl.TableImpl<TCentralRecord>,
        /** Destination jpa entity */
        val dstJpaEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,

        /** Source sync id jooq field */
        srcJooqSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
        /** Destination sync id field */
        val dstJpaSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,

        /** Conversion function JOOQ record -> JPA entity */
        val srcTransformation: (TCentralRecord) -> TEntity,

        // UID support

        /** Source jooq uid field */
        val srcJooqUidField: org.jooq.TableField<TCentralRecord, ByteArray>? = null,
        /** Destination jpa uid field */
        val dstJpaUidPath: com.querydsl.core.types.dsl.ComparablePath<UUID>? = null,

        /** Conversion function JOOQ record -> JPA entity */
        val dstTransformation: ((TEntity, TCentralRecord) -> Unit)? = null,

        /**
         * Perform accurate delete synchronisation (on each sync interval).
         *
         * By default (false) only outdated sync-ids will be cleaned.
         * REMARK: (true) not recommended for large tables, as the entire index of
         * both source and destination have to be retrieved and diff'ed
         */
        val accurateDeletes: Boolean = false,

        /** Sync interval */
        interval: Duration = Duration.ofMinutes(1)

) : Preset<TCentralRecord>(
        srcJooqTable = srcJooqTable,
        srcJooqSyncIdField = srcJooqSyncIdField,
        interval = interval
) {
    fun toString(fullDescription: Boolean = true): String =
            "Sync [${srcJooqTable.name}${if (fullDescription) " -> ${dstJpaEntityPath.metadata.name}}" else ""}]"

    override fun toString() = this.toString(true)
}

/**
 * Database sync service, synchronizing relational data from
 * a direct (jooq) jdbc source to a jpa destination (and vice versa)
 *
 * Created by masc on 15.05.15.
 */
@Component
class DatabaseSyncService {
    private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)

    /** Sync presets */
    @Inject
    private lateinit var presets: List<Preset<*>>

    //region Source
    @Inject
    private lateinit var srcSyncRepository: JooqSyncRepository

    @Inject
    @Qualifier(org.deku.leoz.central.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var srcTaManager: DataSourceTransactionManager

    /** JOOQ transaction template */
    private val srcTaTemplate by lazy { TransactionTemplate(srcTaManager) }

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var srcDsl: DSLContext
    //endregion

    //region Destination
    @Inject
    private lateinit var dstSyncRepository: SyncRepository

    @Inject
    private lateinit var dstNotifications: JpaNotifications

    @Inject
    private lateinit var dstTaManager: JpaTransactionManager

    /** JPA transaction template */
    private val dstTaTemplate by lazy {
        TransactionTemplate(dstTaManager).also {
            it.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        }
    }

    @PersistenceContext
    private lateinit var dstEm: EntityManager

    @Inject
    private lateinit var dstEmf: EntityManagerFactory
    //endregion

    @Inject
    private lateinit var exceutorService: ScheduledExecutorService

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

    @Deprecated("Superseded by JPA entity notificatinos")
    private val updatesSubject = PublishSubject.create<UpdateEvent>()
    @Deprecated("Superseded by JPA entity notificatinos")
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
                        this.dstSyncRepository.findAll().map {
                            NotificationEvent(
                                    tableName = it.tableName,
                                    localSyncId = it.syncId
                            )
                        }
                )
            }
                    .retryWith(
                            action = { retry, e ->
                                log.error("Notification state retrieval failed [${e.message}]")
                                Observable.timer(1, TimeUnit.MINUTES)
                            }
                    )
                    .concatWith(
                            this.notificationsSubject
                    )
    //endregion

    /**
     * Sync process step
     */
    private class ProcessStep {
        /** Source (central) sync record */
        var srcSyncRecord: JooqSyncRepository.SyncRecord by Delegates.notNull()
        /** Preset to process */
        var preset: Preset<*> by Delegates.notNull()
    }

    @PostConstruct
    @Suppress("UNCHECKED_CAST")
    private fun onInitialize() {

        // 2-way sync support
        this.presets
                .filter {
                    // Criteria for 2-way sync
                    it is SyncPreset<*, *> && it.dstTransformation != null &&
                            it.srcJooqUidField != null
                }
                .forEach { it ->
                    val preset = it as SyncPreset<Record, Any>

                    log.info { preset }

                    this.dstNotifications.subscribePreUpdate(
                            entityType = preset.dstJpaEntityPath.type,
                            transactionManager = srcTaManager,
                            listener = { update ->
                                // masc20180529.
                                // Subscribe via plain unsafe observer as it's mandatory that exceptions wind up to the emitter
                                // so the originating operation is rolled back when synchronisation fails.

                                val syncIdProperty = jpaProperties.getByPath(
                                        preset.dstJpaEntityPath,
                                        preset.dstJpaSyncIdPath as Path<*>
                                )

                                val uidProperty = jpaProperties.getByPath(
                                        preset.dstJpaEntityPath,
                                        preset.dstJpaUidPath as Path<*>
                                )

                                val syncId = syncIdProperty.get(update.value) as Long?
                                val previousSyncid = update.old?.let { syncIdProperty.get(it) } as Long?

                                // When sync-ids differ this update must originate from central
                                if (syncId != previousSyncid) {
                                    log.debug { "Source origin detected, skipping merge of entity update" }
                                    return@subscribePreUpdate
                                }

                                val uid = uidProperty.get(update.value) as UUID

                                log.debug { "Propagating JPA update [${update}]" }

                                when (update.type) {
                                    JpaUpdateType.INSERTED,
                                    JpaUpdateType.UPDATED -> {

                                        val record = srcDsl.selectFrom(preset.srcJooqTable)
                                                .where(preset.srcJooqUidField!!.eq(uid.toByteArray()))
                                                .fetchOne()
                                                ?: srcDsl.newRecord(preset.srcJooqTable)

                                        preset.dstTransformation!!(update.value!!, record)

                                        (record as UpdatableRecord<*>).also {
                                            it.store()

                                            // TODO update sync-id to prevent sync roundtrip
                                            // it.refresh()

                                            // TODO: updating jpa entities during update callbacks causes is _dangerous_, causing inconsistencies
                                            //preset.dstJpaSyncIdProperty.set(update.value, it.get(preset.srcJooqSyncIdField))
                                        }
                                    }

                                    JpaUpdateType.DELETED -> {
                                        srcDsl.deleteFrom(preset.srcJooqTable)
                                                .where(preset.srcJooqUidField!!.eq(uid.toByteArray()))
                                                .execute()
                                    }
                                }
                            })
                }
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
                                this.srcSyncRepository
                                        .findSyncRecords(presets.toSyncSpec())
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
                    .retryWith(
                            action = { retry, e ->
                                log.error("Sync state retrieval failed [${e.message}]")
                                Observable.timer(1, TimeUnit.MINUTES)
                            }
                    )
                    // Group process steps by table name in order to throttle by table
                    .groupBy { it.srcSyncRecord.tableName }
                    .flatMap { tableSteps ->
                        val tableName = tableSteps.key
                        val preset = this.presets.first { it.tableName == tableName }

                        // Throttle accordingly
                        if (preset.interval != null) {
                            tableSteps.throttleLast(
                                    preset.interval.toMillis(),
                                    TimeUnit.MILLISECONDS)
                        } else
                            tableSteps
                    }
                    .doOnNext { step ->
                        val sw = Stopwatch.createStarted()

                        // Actual sync preset processing
                        try {
                            val updated = step.preset.update(
                                    sysSyncRecord = step.srcSyncRecord,
                                    clean = false
                            )

                            val message = { "${step.preset} took ${sw}" }

                            if (updated)
                                log.debug(message)
                            else
                                log.trace(message)
                        } catch (e: Throwable) {
                            if (e.isJooqAccessException())
                                log.error(e.message)
                            else
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

            val syncMap = Stopwatch.createStarted(this, "FINDSYNCIDS", {
                this.srcSyncRepository
                        .findSyncRecords(this.presets.toSyncSpec())
                        .associateBy { it.tableName }
            })

            this.presets.forEach {
                try {
                    it.update(
                            sysSyncRecord = syncMap.getValue(it.tableName),
                            clean = clean
                    )
                } catch (e: Exception) {
                    val message = "${it} failed. ${e.message}"
                    if (e.isJooqAccessException()) log.error(e.message) else log.error(e.message, e)
                }
            }

            log.debug { "Database sync took " + sw.toString() }
        }
                .toCompletable()
    }

    /**
     * Abstract update extension
     *
     * NOTE: this method must be @Synchronized to prevent concurrent updates of the same preset / table
     */
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    private fun Preset<*>.update(
            sysSyncRecord: JooqSyncRepository.SyncRecord,
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
            sysSyncRecord: JooqSyncRepository.SyncRecord,
            clean: Boolean): Boolean {

        val tableName = this.srcJooqTable.name

        return dstTaTemplate.execute { _ ->
            if (clean) {
                dstSyncRepository.deleteAll()
            }

            val dbSyncId = sysSyncRecord.syncIdRange?.endInclusive ?: 0

            val localSync = dstSyncRepository.findOne(
                    lclSync.tableName.eq(tableName)
            )
                    .toNullable()
            // Create new local sync record
                    ?: LclSync().also {
                        it.tableName = tableName
                        it.syncId = -1

                        dstEm.persist(it)
                    }

            if (dbSyncId > localSync.syncId) {
                notificationsSubject.onNext(DatabaseSyncService.NotificationEvent(
                        tableName = tableName,
                        localSyncId = localSync.syncId,
                        syncId = dbSyncId)
                )

                // Update local sync record
                localSync.syncId = dbSyncId
                dstEm.merge(localSync)

                true
            } else {
                false
            }
        }!!
    }

    /** JPA property cache */
    private val jpaProperties = object {
        private val cache = ConcurrentHashMap<PropertyAccess.Key, PropertyAccess>()

        fun getByPath(
                entityPath: EntityPath<*>,
                propertyPath: Path<*>
        ): PropertyAccess {
            val key = PropertyAccess.Key(
                    type = entityPath.type,
                    name = propertyPath.metadata.name
            )

            return cache.getOrPut(key, { PropertyAccess(key) })
        }

        fun getByName(
                type: Class<*>,
                name: String
        ): PropertyAccess {
            val key = PropertyAccess.Key(
                    type = type,
                    name = name
            )

            return cache.getOrPut(key, { PropertyAccess(key) })
        }
    }

    /**
     * Update from sync preset
     * @param clean delete all records before updating
     */
    private fun SyncPreset<Record, Any>.update(
            sysSyncRecord: JooqSyncRepository.SyncRecord,
            clean: Boolean
    ): Boolean {
        // Stopwatch
        val sw = Stopwatch.createStarted()

        // Log formatter
        val lfmt = { s: String -> "${this.toString(false)} ${s}" }

        dstEm.flushMode = FlushModeType.COMMIT

        if (clean || dstJpaSyncIdPath == null) {
            dstTaTemplate.execute<Any> { _ ->
                log.info { lfmt("removing all destination entities") }

                dstEm.truncate(dstJpaEntityPath.type)
                dstEm.flush()
                dstEm.clear()
            }
        }

        //region Determine source & destination sync ranges
        var dstSyncIdRange: LongRange? = null
        if (dstJpaSyncIdPath != null) {
            // Query destination/jpa database table for latest timestamp
            dstSyncIdRange = dstSyncRepository.findSyncIdMinMax(
                    dstJpaEntityPath,
                    dstJpaSyncIdPath
            )
        }

        val srcSyncIdRange = if (srcJooqSyncIdField != null) {
            sysSyncRecord.syncIdRange
        } else null
        //endregion

        log.trace { lfmt("sync-ids source [${srcSyncIdRange}] destination [${dstSyncIdRange}]") }

        //region Update
        if (srcSyncIdRange != null &&
                // Destination is outdated
                (dstSyncIdRange != null && srcSyncIdRange.endInclusive != dstSyncIdRange.endInclusive)
                // Or has no data
                || dstSyncIdRange == null) {

            val dstPkProperty by lazy {
                jpaProperties.getByName(
                        this.dstJpaEntityPath.type,
                        dstEm.metamodel.entity(this.dstJpaEntityPath.type)
                                .getId(java.lang.Long::class.java)
                                .name
                )
            }

            // Get newer records from central
            // masc20150530. JOOQ cursor requires an explicit transaction
            srcTaTemplate.execute<Any> { _ ->
                // Read source records newer than destination timestamp
                srcSyncRepository.findNewerThan(
                        dstSyncIdRange?.endInclusive,
                        srcJooqTable,
                        srcJooqSyncIdField).use { source ->

                    if (source.hasNext()) {
                        // Save to destination/jpa
                        // REMARKS
                        // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                        log.info { lfmt("outdated: sync-ids source [${srcSyncIdRange}] destination [${dstSyncIdRange}]") }

                        var count = 0
                        var skippedDueToNullUid = 0

                        val JPA_FLUSH_BATCH_SIZE = 100
                        val JPA_TRANSACTION_BATCH_SIZE = 10000

                        var transaction: TransactionStatus = dstTaManager.getTransaction(dstTaTemplate)

                        Stopwatch.createStarted(this, lfmt("updating"), {
                            try {
                                while (source.hasNext()) {
                                    // Fetch next record
                                    val record = source.fetchNext()

                                    val pk = if (dstJpaUidPath != null) {
                                        val srcUid = record.get(srcJooqUidField)?.toUUID()

                                        if (srcUid == null) {
                                            skippedDueToNullUid++
                                            continue
                                        }

                                        // Lookup by global uid
                                        dstEm.from(dstJpaEntityPath)
                                                .where(dstJpaUidPath.eq(srcUid))
                                                .fetchOne()
                                                ?.let {
                                                    dstEmf.persistenceUnitUtil.getIdentifier(it)
                                                }
                                    } else null

                                    // Convert to entity
                                    val entity = srcTransformation(record)

                                    if (pk != null) {
                                        dstPkProperty.set(entity, pk)
                                    }

                                    dstEm.merge(entity)

                                    count++

                                    // Flush every now and then (improves performance)
                                    if (count % JPA_FLUSH_BATCH_SIZE == 0) {
                                        dstEm.flush()
                                        dstEm.clear()
                                    }

                                    if (count % JPA_TRANSACTION_BATCH_SIZE == 0) {
                                        dstTaManager.commit(transaction)

                                        // Start new transaction
                                        transaction = dstTaManager.getTransaction(dstTaTemplate)
                                    }
                                }

                                dstEm.flush()
                                dstEm.clear()
                                dstTaManager.commit(transaction)

                            } catch (e: Throwable) {
                                dstTaManager.rollback(transaction)
                                throw e
                            }
                        })

                        // Re-query destination
                        if (dstJpaSyncIdPath != null) {
                            // Query embedded database for updated latest timestamp
                            dstSyncIdRange = dstSyncRepository.findSyncIdMinMax(
                                    dstJpaEntityPath,
                                    dstJpaSyncIdPath
                            )
                        }

                        if (skippedDueToNullUid > 0)
                            log.warn { lfmt("skipped ${skippedDueToNullUid} due to null uid(s)") }

                        log.info { lfmt("updated ${count} [${dstJpaEntityPath.metadata.name}] sync-id(s) [${dstSyncIdRange?.endInclusive}]") }

                        // Emit update event
                        updatesSubject.onNext(
                                DatabaseSyncService.UpdateEvent(
                                        entityType = dstJpaEntityPath.type,
                                        syncId = dstSyncIdRange?.endInclusive
                                )
                        )
                    } else {
                        log.trace { lfmt("uptodate [${dstSyncIdRange?.endInclusive}]") }
                    }
                }
            }
        }
        //endregion

        //region Refresh destination sync range
        if (dstJpaSyncIdPath != null) {
            // Query destination/jpa database table for latest timestamp
            dstSyncIdRange = dstSyncRepository.findSyncIdMinMax(
                    dstJpaEntityPath,
                    dstJpaSyncIdPath
            )
        }
        //endregion

        log.trace { lfmt("post sync-ids source [${srcSyncIdRange}] destination [${dstSyncIdRange}]") }

        //region Auto-delete
        if (srcSyncIdRange != null && dstSyncIdRange != null) {
            if (dstJpaSyncIdPath != null && srcJooqSyncIdField != null) {
                if (dstSyncIdRange!!.first < srcSyncIdRange.first) {
                    log.info { lfmt("deleting < [${srcSyncIdRange.first}]") }
                    dstTaTemplate.execute<Any> { _ ->
                        dstEm.delete(
                                dstJpaEntityPath,
                                dstJpaSyncIdPath.lt(srcSyncIdRange.first)
                        )
                        dstEm.flush()
                        dstEm.clear()
                    }
                }
            }
        }
        //endregion

        //region Accurate delete
        if (accurateDeletes) {
            if (srcJooqSyncIdField != null && dstJpaSyncIdPath != null) {
                val srcCount = Stopwatch.createStarted(this, lfmt("SRC_FETCHCOUNT"), {
                    srcSyncRepository.count(srcJooqTable)
                })

                val dstCount = Stopwatch.createStarted(this, lfmt("DST_FETCHCOUNT"), {
                    dstSyncRepository.count(dstJpaEntityPath)
                })

                if (srcCount != dstCount) {
                    log.info { lfmt("performing accurate delete due to source [${srcCount}] <> [${dstCount}]") }

                    val srcSyncIds = Stopwatch.createStarted(this, lfmt("SRC_FINDALLSYNCIDS"), {
                        srcSyncRepository.findAllSyncIds(
                                srcJooqTable,
                                srcJooqSyncIdField
                        )
                    })

                    val dstSyncIds = Stopwatch.createStarted(this, lfmt("DST_FINDALLSYNCIDS"), {
                        dstSyncRepository.findAllSyncIds(
                                dstJpaEntityPath,
                                dstJpaSyncIdPath
                        )
                    })

                    val toDelete = dstSyncIds.subtract(srcSyncIds)

                    log.info { lfmt("deleting ${toDelete.count()} entities") }

                    dstTaTemplate.execute { _ ->
                        dstEm.delete(
                                dstJpaEntityPath,
                                dstJpaSyncIdPath.`in`(toDelete)
                        )
                    }
                }
            }
        }
        //endregion

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
