package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.SYS_SYNC
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SysSyncRecord
import org.deku.leoz.central.data.prepared
import org.jooq.*
import org.jooq.impl.DSL
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
     * Prepared statement
     */
    private val qFindSyncIdByTableName by lazy {
        dsl
                .select(SYS_SYNC.SYNC_ID)
                .from(SYS_SYNC)
                .where(SYS_SYNC.TABLE_NAME.eq(
                        DSL.param(SYS_SYNC.TABLE_NAME.name, String::class.java)))
    }

    private val qFindAll by lazy {
        dsl.selectFrom(SYS_SYNC)
    }

    /**
     * Find sync id by table name
     * @param tableName Table name
     */
    fun findSyncIdByTableName(tableName: String): Long? {
        return this.qFindSyncIdByTableName.prepared {
            val query = it.bind(SYS_SYNC.TABLE_NAME.name, tableName)

            query.fetchOne()?.value1()
        }
    }

    /**
     * Fetch all sync records
     */
    fun findAll(): List<SysSyncRecord> {
        return this.qFindAll.prepared {
            it.fetch().toList()
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
     * Find maximum sync id for central table
     * @param table Jooq table
     * @param field Jooq sync id field
     */
    fun <TRecord : Record> findMaxSyncId(
            table: TableImpl<TRecord>,
            field: TableField<out TRecord, Long>
    ): Long? {
        return dsl.selectFrom(table)
                .fetchOne(field.max())
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
     * Find minimum and maximum sync id
     * @param table table
     * @param syncIdField sync id field
     * @return range of sync ids (min..max) or null when table is empty
     */
    fun <TRecord : Record> findMinMaxSyncId(
            table: TableImpl<TRecord>,
            syncIdField: TableField<out TRecord, Long>): LongRange? {

        return dsl.select(syncIdField.min(), syncIdField.max())
                .from(table)
                .fetchOne()
                .let {
                    val min = it.component1() ?: return null
                    val max = it.component2() ?: return null

                    (min..max)
                }
    }
}