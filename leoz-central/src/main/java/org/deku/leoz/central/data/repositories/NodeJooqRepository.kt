package org.deku.leoz.central.data.repositories

import org.deku.leoz.central.data.entities.jooq.Tables
import org.deku.leoz.central.data.entities.jooq.tables.MstNode
import org.deku.leoz.central.data.entities.jooq.tables.records.MstNodeRecord
import org.jooq.DSLContext

import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 02.07.15.
 */
@Named
class NodeJooqRepository {
    @Inject
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
        val r = this.findByKey(key)
        return r != null && r.authorized !== 0
    }

    /**
     * Find node record by key or create new one if there's none
     * @param key
     */
    fun findByKeyOrCreateNew(key: String): MstNodeRecord {
        var r = this.findByKey(key)
        return r ?: dslContext.newRecord(Tables.MST_NODE)
    }
}
