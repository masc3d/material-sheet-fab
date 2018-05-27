package org.deku.leoz.node.data

import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.repository.StationRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by masc on 15.05.15.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
class StationRepositoryTest {
    @Inject
    private lateinit var stationRepository: StationRepository

    @Test
    fun testFindAll() {
        for (d in stationRepository.findAll()) {
            println(d)
        }
    }

    @Test
    fun testFindWithQuery() {
        for (d in stationRepository.findWithQuery("DEKU")) {
            println(d)
        }
    }

}
