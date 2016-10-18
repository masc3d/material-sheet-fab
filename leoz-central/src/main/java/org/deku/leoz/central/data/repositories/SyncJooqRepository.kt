package org.deku.leoz.central.data.repositories

import org.deku.leoz.central.data.entities.jooq.Tables
import org.deku.leoz.central.data.entities.jooq.tables.records.SysSyncRecord
import org.jooq.DSLContext
import org.jooq.Param
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 18/10/2016.
 */
@Named
class SyncJooqRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var dslContext: DSLContext

    /**
     * Prepared statement
     */
    val qFindSyncIdByTableName by lazy {
        dslContext
                .select(Tables.SYS_SYNC.SYNC_ID)
                .from(Tables.SYS_SYNC)
                .where(Tables.SYS_SYNC.TABLE_NAME.eq(
                        DSL.param(Tables.SYS_SYNC.TABLE_NAME.name, String::class.java)))
                .keepStatement(true)
    }

    /**
     * Find sync id by table name
     * @param tableName Table name
     */
    fun findSyncIdByTableName(tableName: String): Long {
        return this.qFindSyncIdByTableName
                .bind(Tables.SYS_SYNC.TABLE_NAME.name, tableName)
                .fetchOne()
                .value1()
    }

    @PreDestroy
    fun onDestroy() {
        qFindSyncIdByTableName.close()
    }
}