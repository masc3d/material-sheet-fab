package org.deku.leoz.node.data.repository.master

import org.deku.leoz.node.data.jpa.MstStationSector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

/**
 * Station sector repository
 * Created by JT on 29.06.15.
 */
interface StationSectorRepository :
        JpaRepository<MstStationSector, Long>,
        QuerydslPredicateExecutor<MstStationSector>
