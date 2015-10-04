package org.deku.leoz.node.data.repositories.master;

import com.mysema.query.types.Predicate;
import org.deku.leoz.node.data.entities.master.Route;
import org.deku.leoz.node.data.repositories.master.custom.RouteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import javax.persistence.QueryHint;

/**
 * Created by masc on 16.05.15.
 */
public interface RouteRepository extends JpaRepository<Route, Long>, QueryDslPredicateExecutor<Route>, RouteRepositoryCustom {
//    @QueryHints(value = {
//            @QueryHint(name = org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE, value = "true"),
//            @QueryHint(name = org.eclipse.persistence.config.QueryHints.QUERY_RESULTS_CACHE_SIZE, value = "500")
//    }, forCounting = false)
    Iterable<Route> findAll(Predicate predicate);
}
