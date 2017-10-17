package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.SysSyncRecord
import org.deku.leoz.central.data.prepared
import org.jooq.DSLContext
import org.jooq.impl.DSL
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
class SyncJooqRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    /**
     * Prepared statement
     */
    private val qFindSyncIdByTableName by lazy {
        dslContext
                .select(Tables.SYS_SYNC.SYNC_ID)
                .from(Tables.SYS_SYNC)
                .where(Tables.SYS_SYNC.TABLE_NAME.eq(
                        DSL.param(Tables.SYS_SYNC.TABLE_NAME.name, String::class.java)))
    }

    private val qFindAll by lazy {
        dslContext.selectFrom(Tables.SYS_SYNC)
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
}