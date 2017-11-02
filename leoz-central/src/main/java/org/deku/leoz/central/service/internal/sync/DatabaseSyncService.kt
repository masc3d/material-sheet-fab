package org.deku.leoz.central.service.internal.sync

import com.querydsl.jpa.impl.JPADeleteClause
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.threeten.bp.Duration
import sx.concurrent.Service
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

    /**
     * Database sync preset
     */
    interface Preset {}

    /**
     * Database sync preset
     * @param <TEntity>             Type of destiantion JPA entity
     * @param <TCentralRecord>      Type of source JOOQ record
     * @property sourceTable           JOOQ source table
     * @property sourceTableSyncIdField      JOOQ source sync id field
     * @property destQdslEntityPath    Destination QueryDSL entity table path
     * @property destQdslSyncIdPath Destination QueryDSL sync id field path
     * @property conversionFunction    Conversion function JOOQ record -> JPA entity
     */
    class SimplePreset<TCentralRecord : org.jooq.Record, TEntity>(
            val sourceTable: org.jooq.impl.TableImpl<TCentralRecord>,
            val sourceTableSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
            val destQdslEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            val destQdslSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
            val conversionFunction: (TCentralRecord) -> TEntity
    ) : Preset

    //region Events
    interface EventListener : sx.event.EventListener {
        /** Emitted when entities have been updated  */
        fun onUpdate(entityType: Class<out Any?>, currentSyncId: Long?)
    }

    private val eventDispatcher = sx.event.EventDispatcher.Companion.createThreadSafe<EventListener>()
    open val eventDelegate: sx.event.EventDelegate<EventListener>
        get() = eventDispatcher
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
    private lateinit var genericJooqRepository: org.deku.leoz.central.data.repository.GenericJooqRepository

    @Inject
    private lateinit var syncJooqRepository: org.deku.leoz.central.data.repository.SyncJooqRepository

    @Transactional(value = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    @Synchronized open fun sync(clean: Boolean) {
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
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }

        log.info("Database sync took " + sw.toString())
    }

    /**
     * Generic updater for entites from jooq to jpa
     * @param deleteBeforeUpdate    Delete all records before updating
     */
    fun update(preset: SimplePreset<Record, Any>,
               syncIdMap: Map<String, Long>,
               deleteBeforeUpdate: Boolean
    ) {
        preset.also { p ->
            // Stopwatch
            val sw = com.google.common.base.Stopwatch.createStarted()
            // Log formatter
            val lfmt = { s: String -> "[${p.destQdslEntityPath.type.name}] ${s} $sw" }

            entityManager.flushMode = javax.persistence.FlushModeType.COMMIT

            if (deleteBeforeUpdate || p.destQdslSyncIdPath == null) {
                transactionJpa.execute<Any> { _ ->
                    log.info(lfmt("Deleting all entities"))

                    JPADeleteClause(entityManager, p.destQdslEntityPath)
                            .execute()

                    entityManager.flush()
                    entityManager.clear()
                }
            }

            // TODO. optimize by preparing query or at least caching the querydsl instance
            // Get latest timestamp
            var destMaxSyncId: Long? = null
            if (p.destQdslSyncIdPath != null) {
                // Query embedded database table for latest timestamp
                destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<Any>(entityManager)
                        .from(p.destQdslEntityPath)
                        .select(p.destQdslSyncIdPath.max())
                        .fetchFirst()
            }

            // TODO. optimize by using jooq prepared statements
            if (destMaxSyncId != null) {
                val maxSyncId = syncIdMap.get(p.sourceTable.name) ?: throw IllegalStateException("No sync id map entry for [${p.sourceTable.name}]")
                if (maxSyncId == destMaxSyncId) {
                    log.trace(lfmt("sync-id uptodate [${destMaxSyncId}]"))
                    return
                }
            }

            // Get newer records from central
            // masc20150530. JOOQ cursor requires an explicit transaction
            transactionJooq.execute<Any> { _ ->
                // Read source records newer than destination timestamp
                val source = genericJooqRepository.findNewerThan(
                        destMaxSyncId,
                        p.sourceTable,
                        p.sourceTableSyncIdField)

                if (source.hasNext()) {
                    // Save to destination/jpa
                    // REMARKS
                    // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                    log.info(lfmt("Outdated [${destMaxSyncId}]"))
                    var count = 0
                    transactionJpa.execute<Any> { _ ->
                        while (source.hasNext()) {
                            // Fetch next record
                            val record = source.fetchNext()
                            // Convert to entity
                            val entity = p.conversionFunction(record)
                            // Store entity
                            entityManager.merge(entity)
                            // Flush every now and then (improves performance)
                            if (count++ % 100 == 0) {
                                try {
                                    entityManager.flush()
                                    entityManager.clear()
                                } catch (e: Exception) {
                                    log.error("SychEntyty " + entity.toString() + " Error: " + e.toString())
                                }
                            }
                        }
                    }

                    // Re-query destination timestamp
                    if (p.destQdslSyncIdPath != null) {
                        // Query embedded database for updated latest timestamp
                        destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<Any>(entityManager)
                                .from(p.destQdslEntityPath)
                                .select(p.destQdslSyncIdPath.max())
                                .fetchFirst()
                    }
                    log.info(lfmt("Updated ${count} entities [${destMaxSyncId}]"))

                    // Emit update event
                    eventDispatcher.emit { e ->
                        // Emit event
                        e.onUpdate(p.destQdslEntityPath.type, destMaxSyncId)
                    }
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
    var interval: Duration
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
