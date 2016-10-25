package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstStationSector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by JT on 29.06.15.
 */
interface StationSectorRepository :
        JpaRepository<MstStationSector, Long>,
        QueryDslPredicateExecutor<MstStationSector>
