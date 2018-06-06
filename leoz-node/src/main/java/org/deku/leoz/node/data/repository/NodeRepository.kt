package org.deku.leoz.node.data.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.MstRoutingLayer
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import sx.text.parseHex
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
        QuerydslPredicateExecutor<MstNode>,
        NodeRepositoryExtension

interface NodeRepositoryExtension {
    fun findByUid(uid: UUID): MstNode?
    fun findByUid(uid: String, strict: Boolean = true): MstNode?
}

class NodeRepositoryImpl : NodeRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var nodeRepo: NodeRepository

    override fun findByUid(uid: UUID): MstNode? =
            nodeRepo.findOne(mstNode.uid.eq(uid)).toNullable()

    override fun findByUid(uid: String, strict: Boolean): MstNode? {
        return nodeRepo.findAll(
                if (strict)
                    mstNode.uid.eq(UUID.fromString(uid))
                else
                    Expressions.booleanTemplate("{0} LIKE '${uid}%'", mstNode.uid)
        ).let {
            when {
                it.count() > 1 -> null
                else -> it.firstOrNull()
            }
        }
    }
}


