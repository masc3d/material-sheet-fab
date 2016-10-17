package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.entities.MstStation
import org.deku.leoz.node.data.repositories.master.custom.StationRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by masc on 30.04.15.
 */
interface StationRepository : JpaRepository<MstStation, Int>, QueryDslPredicateExecutor<MstStation>, StationRepositoryCustom
