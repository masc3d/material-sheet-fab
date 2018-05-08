package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.prepared
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.DSL.inline
import org.jooq.impl.TableImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.sql.ResultSet
import javax.inject.Inject

/**
 * Sync table jooq repository
 * Created by masc on 18/10/2016.
 */
@Component
class JooqSyncRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    /**
     * Specification for a table to sync
     */
    data class SyncSpec<T : Record>(
            /** Jooq table */
            val table: TableImpl<T>,
            /** Jooq sync id field */
            val syncIdField: TableField<out Record, Long>
    )

    /**
     * Sync record (synthetic)
     */
    data class SyncRecord(
            /** Table name */
            val tableName: String,
            /** Sync id range */
            val syncIdRange: LongRange?
    )

    /**
     * Find sync records
     */
    fun findSyncRecords(tables: List<SyncSpec<*>>): List<SyncRecord> {
        if (tables.count() == 0)
            return listOf()

        fun <T : Record> createStatement(spec: SyncSpec<T>): Select<Record3<String, Long, Long>> {
            return dsl.select(
                    inline(spec.table.name),
                    spec.syncIdField.min(),
                    spec.syncIdField.max()
            )
                    .from(spec.table)
        }

        // Build statement
        var stmt = createStatement(tables[0])

        if (tables.count() > 1) {
            tables.subList(1, tables.count())
                    .forEach { stmt = stmt.union(createStatement(it)) }
        }

        return stmt.toList().map {
            val tableName = it.value1()
            val min = it.value2()
            val max = it.value3()

            SyncRecord(
                    tableName,
                    if (min != null && max != null)
                        LongRange(min, max)
                    else
                        null
            )
        }
    }

    /**
     * Generic find newer function
     * @param syncId Optional sync id. If omitted all records are fetched.
     * @param table Jooq table
     * @param syncIdField Jooq field
     * @return Jooq record
     */
    fun <TRecord : Record> findNewerThan(
            syncId: Long?,
            table: TableImpl<TRecord>,
            syncIdField: TableField<out TRecord, Long>?): Cursor<TRecord> {

        return dsl.selectFrom(table)
                .let {
                    when {
                        (syncId != null && syncIdField != null) -> it.where(syncIdField.gt(syncId))
                        else -> it
                    }
                }
                .resultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
                .resultSetType(ResultSet.TYPE_FORWARD_ONLY)
                .fetchSize(Int.MIN_VALUE)
                .fetchLazy()
    }

    /**
     * Find newer sync ids
     * @param syncId Sync id
     * @param table Table
     * @param syncIdField Table sync id field
     */
    fun <TRecord : Record> findSyncIdsNewerThan(
            syncId: Long,
            table: TableImpl<TRecord>,
            syncIdField: TableField<out TRecord, Long>): Cursor<Record1<Long>> {

        return dsl.select(syncIdField)
                .from(table)
                .where(syncIdField.gt(syncId))
                .resultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
                .resultSetType(ResultSet.TYPE_FORWARD_ONLY)
                .fetchSize(Int.MIN_VALUE)
                .fetchLazy()
    }

    /**
     * Fetch all sync ids
     * @param table table
     * @param syncIdField sync-id field
     */
    fun <TRecord : Record> findAllSyncIds(
            table: TableImpl<TRecord>,
            syncIdField: TableField<out TRecord, Long>): Set<Long> {

        return dsl.select(syncIdField)
                .from(table)
                .fetchInto(Long::class.java)
                .toSet()
    }

    fun <TRecord : Record> count(
            table: TableImpl<TRecord>
    ): Long {
        return dsl.fetchCount(table).toLong()
    }
}