package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstKey
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.util.toNullable
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Key repository
 */
interface KeyRepository :
        JpaRepository<MstKey, Long>,
        QuerydslPredicateExecutor<MstKey>, KeyRepositoryExtension

interface KeyRepositoryExtension {
}

class KeyRepositoryImpl : NodeRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var nodeRepo: NodeRepository

    override fun findByUid(nodeUid: String): MstNode? {
        return nodeRepo.findOne(
                mstNode.key.startsWith(nodeUid)
        ).toNullable()
    }
    override fun findByKey(key: String): MstNode? {
        return nodeRepo.findOne(mstNode.key.eq(key)).toNullable()
    }
}


