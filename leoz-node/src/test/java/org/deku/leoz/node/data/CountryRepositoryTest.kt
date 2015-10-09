package org.deku.leoz.node.data

import org.deku.leoz.node.DataTest
import org.deku.leoz.node.data.entities.master.QCountry
import org.deku.leoz.node.data.repositories.master.CountryRepository
import org.junit.Test
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Created by masc on 05.10.15.
 */
class CountryRepositoryTest : DataTest() {
    @Inject
    lateinit private var countryRepository: CountryRepository

    @Test
    fun test() {
        val country = countryRepository.findOne("DE")

        val ts = Timestamp.valueOf(LocalDateTime.of(2014, 1, 1, 0, 0))
        for (c in countryRepository.findAll(QCountry.country.timestamp.after(ts))) {
            println(c.timestamp)
        }
    }
}