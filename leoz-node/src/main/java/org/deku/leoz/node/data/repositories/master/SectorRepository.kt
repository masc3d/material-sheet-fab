package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstSector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by masc on 16.05.15.
 */
interface SectorRepository :
        JpaRepository<MstSector, Long>,
        QueryDslPredicateExecutor<MstSector>
