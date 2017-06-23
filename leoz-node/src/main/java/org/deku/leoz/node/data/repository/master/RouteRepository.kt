package org.deku.leoz.node.data.repository.master

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
        RouteRepositoryExtension {
    @QueryHints(
            // masc20170622. CACHING EXAMPLE
            // IMPORTANT. cache options are very restrictive and documentation of eg. CacheUsage
            // has to be read carefully. eg. enabling a single entity cache on findAll will silently
            // enforce the query to return the first result only.
            QueryHint(name = org.eclipse.persistence.config.QueryHints.CACHE_USAGE, value = CacheUsage.DoNotCheckCache)
    )
    override fun findAll(predicate: Predicate): Iterable<MstRoute>
}

interface RouteRepositoryExtension

class RouteRepositoryImpl : RouteRepositoryExtension {
    @Inject
    private lateinit var routeRepository: RouteRepository
}
