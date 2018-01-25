package org.deku.leoz.node.data.repository

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Param
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.jpa.QMstRoute
import org.eclipse.persistence.config.CacheUsage
import org.eclipse.persistence.config.HintValues
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.jpa.NamedQuery
import java.sql.Timestamp
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.QueryHint

/**
 * Route repository
 * Created by masc on 16.05.15.
 */
interface RouteRepository :
        JpaRepository<MstRoute, Long>,
        QuerydslPredicateExecutor<MstRoute>,
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

interface RouteRepositoryExtension {
    class FindRouteQueryParams {
        val layer = Param(Int::class.java)
        val country = Param(String::class.java)
        val zip = Param(String::class.java)
        val validDate = Param(Timestamp::class.java)
    }

    val findRouteQuery: NamedQuery<MstRoute, FindRouteQueryParams>
}

class RouteRepositoryImpl : RouteRepositoryExtension {
    @Inject
    private lateinit var routeRepository: RouteRepository

    @Inject
    private lateinit var entityManager: EntityManager

    override val findRouteQuery by lazy {
        NamedQuery(
                entityManager = entityManager,
                resultType = MstRoute::class.java,
                paramsType = RouteRepositoryExtension.FindRouteQueryParams::class.java,
                hints = listOf(
                        Pair(org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE),
                        Pair(org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE_SIZE, 500.toString())
                ),
                query = { q, p ->
                    val qRoute = QMstRoute.mstRoute

                    q.from(qRoute)
                            .where(qRoute.layer.eq(p.layer)
                                    .and(qRoute.country.eq(p.country))
                                    .and(qRoute.zipFrom.loe(p.zip))
                                    .and(qRoute.zipTo.goe(p.zip))
                                    .and(qRoute.validFrom.before(p.validDate))
                                    .and(qRoute.validTo.after(p.validDate))
                            )
                }
        )
    }
}
