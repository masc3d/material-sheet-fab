package org.deku.leoz.node.data

import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.QMstCountry
import org.deku.leoz.node.data.repository.CountryRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Created by masc on 05.10.15.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
class CountryRepositoryTest {
    @Inject
    private lateinit var countryRepository: CountryRepository

    @Test
    fun test() {
        countryRepository.findById("DE")

        val ts = Timestamp.valueOf(LocalDateTime.of(2014, 1, 1, 0, 0))
        for (c in countryRepository.findAll(QMstCountry.mstCountry.timestamp.after(ts))) {
            println(c.timestamp)
        }
    }
}