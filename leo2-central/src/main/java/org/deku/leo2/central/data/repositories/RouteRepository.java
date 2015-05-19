package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by masc on 16.05.15.
 */
public interface RouteRepository extends JpaRepository<Route, Integer>, QueryDslPredicateExecutor {
}
