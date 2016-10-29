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

/**
 * Created by JT on 13.05.15.
 */
interface RouteRepositoryCustom

/**
 * Created by JT on 13.05.15.
 */
class RouteRepositoryImpl : RouteRepositoryCustom {
    @Inject
    private lateinit var mRouteRepository: RouteRepository
}
