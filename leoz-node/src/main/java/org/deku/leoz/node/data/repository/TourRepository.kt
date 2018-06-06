package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.QTadTour
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Tour repository
 * Created by masc on 25.01.18.
 */
interface TadTourRepository :
        JpaRepository<TadTour, Long>,
        QuerydslPredicateExecutor<TadTour>,
        TadTourRepositoryExtension

interface TadTourRepositoryExtension {
    fun findMostRecentByUserId(userId: Long): TadTour?
}

class TadTourRepositoryImpl : TadTourRepositoryExtension {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findMostRecentByUserId(userId: Long): TadTour? {
        return em.from(tadTour)
                .where(tadTour.userId.eq(userId))
                .orderBy(tadTour.modified.desc())
                .fetchFirst()
    }
}

/**
 * Tour entry repository
 * Created by masc on 25.01.18.
 */
interface TadTourEntryRepository :
        JpaRepository<TadTourEntry, Long>,
        QuerydslPredicateExecutor<TadTourEntry>

