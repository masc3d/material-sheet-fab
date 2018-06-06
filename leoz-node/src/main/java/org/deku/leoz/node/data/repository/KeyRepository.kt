package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstKey
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.util.toNullable
import java.util.*
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

class KeyRepositoryImpl : KeyRepositoryExtension {

}


