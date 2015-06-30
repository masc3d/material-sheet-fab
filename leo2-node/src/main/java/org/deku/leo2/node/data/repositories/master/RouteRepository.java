package org.deku.leo2.node.data.repositories.master;

import org.deku.leo2.node.data.entities.master.Route;
import org.deku.leo2.node.data.entities.master.RoutePK;
import org.deku.leo2.node.data.repositories.master.custom.RouteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 16.05.15.
 */
public interface RouteRepository extends JpaRepository<Route, RoutePK>, QueryDslPredicateExecutor, RouteRepositoryCustom {
}
