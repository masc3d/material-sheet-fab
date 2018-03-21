package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.MstRoutingLayer
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.util.toNullable
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Node repository
 */
interface NodeRepository :
        JpaRepository<MstNode, Int>,
        QuerydslPredicateExecutor<MstNode>, NodeRepositoryExtension

interface NodeRepositoryExtension {
    /**
     * Find node by uid or truncated uid
     * @param nodeUid node uid to find. this can be a truncated or short uid.
     */
    fun findByUid(nodeUid: String): MstNode?
}

class NodeRepositoryImpl : NodeRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var nodeRepo: NodeRepository

    override fun findByUid(nodeUid: String): MstNode? {
        return nodeRepo.findOne(
                mstNode.key.startsWith(nodeUid)
        ).toNullable()
    }
}


