package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstRoutingLayer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Routing layer repository
 * Created by JT on 23.06.15.
 */
interface RoutingLayerRepository :
        JpaRepository<MstRoutingLayer, Int>,
        QueryDslPredicateExecutor<MstRoutingLayer>


