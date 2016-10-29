package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstCountry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import javax.inject.Inject

/**
 * Country repository
 * Created by masc on 11.05.15.
 */
interface CountryRepository :
        JpaRepository<MstCountry, String>,
        QueryDslPredicateExecutor<MstCountry>,
        CountryRepositoryCustom


interface CountryRepositoryCustom

class CountryRepositoryImpl : CountryRepositoryCustom {
    @Inject
    private lateinit var routeRepository: CountryRepository
}
