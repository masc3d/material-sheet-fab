package org.deku.leoz.node.data.repositories.master.custom

import org.deku.leoz.node.data.repositories.master.CountryRepository
import javax.inject.Inject

/**
 * Created by JT on 13.05.15.
 */
interface CountryRepositoryCustom

/**
 * Created by JT on 12.05.15.
 */
class CountryRepositoryImpl : CountryRepositoryCustom {
    @Inject
    private lateinit var routeRepository: CountryRepository
}
