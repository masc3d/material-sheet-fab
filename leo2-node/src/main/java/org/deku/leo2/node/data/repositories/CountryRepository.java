package org.deku.leo2.node.data.repositories;

import org.deku.leo2.node.data.entities.Country;
import org.deku.leo2.node.data.repositories.custom.CountryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 11.05.15.
 */
public interface CountryRepository extends JpaRepository<Country, String>, QueryDslPredicateExecutor, CountryRepositoryCustom {
}

