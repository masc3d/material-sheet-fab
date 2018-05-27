package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.MstRoutingLayer
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import sx.util.toNullable
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Node repository
 */
interface NodeRepository :
        JpaRepository<MstNode, Int>,
        QuerydslPredicateExecutor<MstNode>, NodeRepositoryExtension, NodeRepositoryExtensionGeneral

interface NodeRepositoryExtension {
    /**
     * Find node by uid or truncated uid
     * @param nodeUid node uid to find. this can be a truncated or short uid.
     */
    fun findByPartUid(nodeUid: String): MstNode?

}

interface NodeRepositoryExtensionGeneral {
    fun findByUid(uid: UUID): MstNode?
}

class NodeRepositoryImpl : NodeRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var nodeRepo: NodeRepository

    override fun findByPartUid(nodeUid: String): MstNode? {
        try {
            val uid = UUID.fromString(nodeUid)
            return nodeRepo.findByUid(uid)
        } catch (e: Exception) {
            val uidList = em.from(mstNode).select(mstNode.uid).fetch()
            uidList ?: return null
            val uidStringList = uidList.map { it.toString() }.toList()
            if (uidStringList.count { it.startsWith(nodeUid) } > 1) return null
            val uid = uidList.firstOrNull {
                it.toString().startsWith(nodeUid)
            }
            uid ?: return null
            return nodeRepo.findByUid(uid)
        }

    }

}


