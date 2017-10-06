package org.deku.leoz.node.data.repository.master

import org.deku.leoz.node.data.jpa.MstDebitorStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface DebitorStationRepository :
        JpaRepository<MstDebitorStation, Int>,
        QueryDslPredicateExecutor<MstDebitorStation>