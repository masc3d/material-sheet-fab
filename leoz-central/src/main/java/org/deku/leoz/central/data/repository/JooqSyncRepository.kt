package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SysSyncRecord
import org.deku.leoz.central.data.prepared
import org.jooq.Cursor
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.Stopwatch
import javax.inject.Inject
import javax.inject.Named

/**
 * Sync table jooq repository
 * Created by masc on 18/10/2016.
 */
@Named
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
                .select(Tables.SYS_SYNC.SYNC_ID)
                .from(Tables.SYS_SYNC)
                .where(Tables.SYS_SYNC.TABLE_NAME.eq(
                        DSL.param(Tables.SYS_SYNC.TABLE_NAME.name, String::class.java)))
    }

    private val qFindAll by lazy {
        dsl.selectFrom(Tables.SYS_SYNC)
    }

    /**
     * Find sync id by table name
     * @param tableName Table name
     */
    fun findSyncIdByTableName(tableName: String): Long? {
        return this.qFindSyncIdByTableName.prepared {
            val query = it.bind(Tables.SYS_SYNC.TABLE_NAME.name, tableName)

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
     * @param field Jooq field
     * @return Jooq record
     */
    fun <TRecord : Record> findNewerThan(
            syncId: Long?,
            table: TableImpl<TRecord>,
            field: TableField<out Record, Long>?): Cursor<TRecord> {

        return dsl.selectFrom(table)
                .where(if ((syncId != null && field != null)) field.gt(syncId) else DSL.trueCondition())
                .fetchLazy()
    }
}