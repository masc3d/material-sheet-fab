package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.entities.MstCountry
import org.deku.leoz.node.data.repositories.master.custom.CountryRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by JT on 11.05.15.
 */
interface CountryRepository : JpaRepository<MstCountry, String>, QueryDslPredicateExecutor<MstCountry>, CountryRepositoryCustom

