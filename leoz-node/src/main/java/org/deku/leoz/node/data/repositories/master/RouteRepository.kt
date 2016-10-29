package org.deku.leoz.node.data.repositories.master

import com.querydsl.core.types.Predicate
import org.deku.leoz.node.data.jpa.MstRoute
import org.eclipse.persistence.config.CacheUsage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import javax.inject.Inject
import javax.persistence.QueryHint

/**
 * Route repository
 * Created by masc on 16.05.15.
 */
interface RouteRepository :
        JpaRepository<MstRoute, Long>,
        QueryDslPredicateExecutor<MstRoute>,
        RouteRepositoryCustom {
    @QueryHints(
            QueryHint(name = org.eclipse.persistence.config.QueryHints.CACHE_USAGE, value = CacheUsage.CheckCacheThenDatabase)
    )
    override fun findAll(predicate: Predicate): Iterable<MstRoute>
}

interface RouteRepositoryCustom

class RouteRepositoryImpl : RouteRepositoryCustom {
    @Inject
    private lateinit var mRouteRepository: RouteRepository
}
