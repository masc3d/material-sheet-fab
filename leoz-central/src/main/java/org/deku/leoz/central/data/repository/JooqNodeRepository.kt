package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.jooq.dekuclient.tables.MstNode
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstNodeRecord
import org.jooq.DSLContext
import org.jooq.SelectWhereStep
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.inject.Inject

/**
 * Created by masc on 02.07.15.
 */
@Component
class JooqNodeRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    /**
     * Create new record
     */
    fun createNew(): MstNodeRecord {
        return dsl.newRecord(Tables.MST_NODE)
    }

    /**
     * Find node by id
     * @param id Node id
     */
    fun findById(id: Int): MstNodeRecord? {
        return dsl.selectFrom(MST_NODE).fetchById(id)
    }

    /**
     * Find node record by key
     * @param key Key
     * @return Node record
     */
    fun findByKey(key: String): MstNodeRecord? {
        return dsl.selectFrom(MstNode.MST_NODE).fetchByUid(key)
    }

    /**
     * Find node record by key starting with
     * @param partialKey Partial key
     */
    fun findByUid(nodeUid: String, strict: Boolean = true ): MstNodeRecord? {
        return dsl.selectFrom(MstNode.MST_NODE)
                .fetchByUid(nodeUid, strict)
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
        return this.findByKey(key) ?: dsl.newRecord(Tables.MST_NODE)
    }
}

val MstNodeRecord.uid: String
    get() = this.key

/**
 * Fetch node record by uid
 * @param nodeUid Node uid
 * @param strict Allows lookups by short/truncated uid as long as the result is unique
 */
fun SelectWhereStep<MstNodeRecord>.fetchByUid(nodeUid: String, strict: Boolean = true): MstNodeRecord? {
    return when {
        strict -> {
            // Only allow precise matches
            this.where(MST_NODE.KEY.eq(nodeUid)).fetchOne()
        }
        else -> {
            // Allow short/truncated uids
            this.where(MST_NODE.KEY.startsWith(nodeUid)).toList().let {
                when {
                    it.count() > 1 -> null
                    else -> it.firstOrNull()
                }
            }
        }
    }
}

fun SelectWhereStep<MstNodeRecord>.fetchById(id: Int): MstNodeRecord? {
    return this.where(MST_NODE.NODE_ID.eq(id)).fetchOne()
}

fun SelectWhereStep<MstNodeRecord>.fetchIdByUid(uid: String):Int? {
    return this.where(MST_NODE.KEY.eq(uid))
            .fetchOne(MST_NODE.NODE_ID)
}

fun SelectWhereStep<MstNodeRecord>.fetchUidById(id: Int): String? {
    return this.where(MST_NODE.NODE_ID.eq(id))
            .fetchOne(MST_NODE.KEY)
}

fun SelectWhereStep<MstNodeRecord>.fetchIsAuthorized(nodeUid: String): Boolean {
    return this.fetchByUid(nodeUid)?.authorized ?: 0 != 0
}