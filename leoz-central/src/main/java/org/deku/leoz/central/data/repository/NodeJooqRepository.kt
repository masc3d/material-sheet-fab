package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.MstNode
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstNodeRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier

import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 02.07.15.
 */
@Named
class NodeJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    /**
     * Create new record
     */
    fun createNew(): MstNodeRecord {
        return dslContext.newRecord(Tables.MST_NODE)
    }

    /**
     * Find node record by key
     * @param key Key
     * @return Node record
     */
    fun findByKey(key: String): MstNodeRecord? {
        return dslContext.fetchOne(MstNode.MST_NODE, Tables.MST_NODE.KEY.eq(key))
    }

    /**
     * Find node record by key starting with
     * @param partialKey Partial key
     */
    fun findByKeyStartingWith(partialKey: String): MstNodeRecord? {
        return dslContext.fetchOne(MstNode.MST_NODE, Tables.MST_NODE.KEY.startsWith(partialKey))
    }

    /**
     * Check if there's a record for a specific key
     * @param key
     * @return
     */
    fun hasAuthorizedKey(key: String): Boolean {
        return this.findByKey(key)?.authorized ?: 0 != 0
    }

    /**
     * Find node record by key or create new one if there's none
     * @param key
     */
    fun findByKeyOrCreateNew(key: String): MstNodeRecord {
        return this.findByKey(key) ?: dslContext.newRecord(Tables.MST_NODE)
    }
}
