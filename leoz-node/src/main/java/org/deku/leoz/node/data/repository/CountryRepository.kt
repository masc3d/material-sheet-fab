package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstCountry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.inject.Inject

/**
 * Country repository
 * Created by masc on 11.05.15.
 */
interface CountryRepository :
        JpaRepository<MstCountry, String>,
        QuerydslPredicateExecutor<MstCountry>,
        CountryRepositoryExtension


interface CountryRepositoryExtension

class CountryRepositoryImpl : CountryRepositoryExtension {
    @Inject
    private lateinit var routeRepository: CountryRepository
}
