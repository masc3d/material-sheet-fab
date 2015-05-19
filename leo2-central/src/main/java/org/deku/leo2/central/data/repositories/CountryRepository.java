package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Country;
import org.deku.leo2.central.data.repositories.custom.CountryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by JT on 11.05.15.
 */
public interface CountryRepository extends JpaRepository<Country, String>, QueryDslPredicateExecutor {
}

