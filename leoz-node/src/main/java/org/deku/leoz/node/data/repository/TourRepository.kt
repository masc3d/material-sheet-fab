package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstStationSector
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

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
        QuerydslPredicateExecutor<TadTourEntry>
