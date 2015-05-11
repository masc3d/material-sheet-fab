package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Depot;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by masc on 30.04.15.
 */
@Repository
public interface DepotRepository extends CrudRepository<Depot, Integer>, QueryDslPredicateExecutor, DepotRepositoryCustom {
}

