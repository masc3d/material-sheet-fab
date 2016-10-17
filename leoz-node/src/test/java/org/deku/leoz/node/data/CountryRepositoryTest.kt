package org.deku.leoz.node.data

import org.deku.leoz.node.DataTest
import org.deku.leoz.node.data.entities.QMstCountry
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
    private lateinit var countryRepository: CountryRepository

    @Test
    fun test() {
        val country = countryRepository.findOne("DE")

        val ts = Timestamp.valueOf(LocalDateTime.of(2014, 1, 1, 0, 0))
        for (c in countryRepository.findAll(QMstCountry.mstCountry.timestamp.after(ts))) {
            println(c.timestamp)
        }
    }
}