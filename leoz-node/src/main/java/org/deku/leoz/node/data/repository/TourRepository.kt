package org.deku.leoz.node.data.repository

import com.querydsl.core.types.dsl.Param
import org.deku.leoz.node.data.jpa.MstStationSector
import org.deku.leoz.node.data.jpa.QTadTourEntry.tadTourEntry
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.jpa.NamedQuery
import javax.inject.Inject
import javax.persistence.EntityManager

/**
 * Tour repository
 * Created by masc on 25.01.18.
 */
interface TadTourRepository :
        JpaRepository<TadTour, Long>,
        QuerydslPredicateExecutor<TadTour>

/**
 * Tour entry repository
 * Created by masc on 25.01.18.
 */
interface TadTourEntryRepository :
        JpaRepository<TadTourEntry, Long>,
        QuerydslPredicateExecutor<TadTourEntry>,
        TadTourEntryRepositoryEx


interface TadTourEntryRepositoryEx {
}

class TadTourEntryRepositoryExImpl : TadTourEntryRepositoryEx {
}