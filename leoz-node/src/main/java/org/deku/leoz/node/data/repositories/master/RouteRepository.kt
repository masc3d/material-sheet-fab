package org.deku.leoz.node.data.repositories.master

import com.querydsl.core.types.Predicate
import org.deku.leoz.node.data.entities.MstRoute
import org.deku.leoz.node.data.repositories.master.custom.RouteRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by masc on 16.05.15.
 */
interface RouteRepository :
        JpaRepository<MstRoute, Long>,
        QueryDslPredicateExecutor<MstRoute>,
        RouteRepositoryCustom {
    // TODO: preliminary test for applying jpa query hints to spring-data queries. don't remove (just yet)
    //    @QueryHints(value = {
    //            @QueryHint(name = org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE, value = "true"),
    //            @QueryHint(name = org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE_SIZE, value = "500")
    //    }, forCounting = false)
    override fun findAll(predicate: Predicate): Iterable<MstRoute>
}
