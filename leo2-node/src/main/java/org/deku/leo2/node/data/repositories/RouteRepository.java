package org.deku.leo2.node.data.repositories;

import org.deku.leo2.node.data.entities.Route;
import org.deku.leo2.node.data.entities.RoutePK;
import org.deku.leo2.node.data.repositories.custom.RouteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 16.05.15.
 */
public interface RouteRepository extends JpaRepository<Route, RoutePK>, QueryDslPredicateExecutor, RouteRepositoryCustom {
}
