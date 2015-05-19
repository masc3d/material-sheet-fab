package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Route;
import org.deku.leo2.central.data.entities.RoutePK;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by JT on 13.05.15.
 */
@Repository
public interface RouteRepository extends CrudRepository<Route,RoutePK>,QueryDslPredicateExecutor,RouteRepositoryCostum {

}
