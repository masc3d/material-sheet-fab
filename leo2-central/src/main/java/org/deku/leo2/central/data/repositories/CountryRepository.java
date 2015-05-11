package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Depot;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by JT on 11.05.15.
 */
@Repository
public interface CountryRepository extends CrudRepository<Depot, Integer>, QueryDslPredicateExecutor {
}

