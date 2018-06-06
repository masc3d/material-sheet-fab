package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstSector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

/**
 * Sector repository
 * Created by masc on 16.05.15.
 */
interface SectorRepository :
        JpaRepository<MstSector, Long>,
        QuerydslPredicateExecutor<MstSector>
