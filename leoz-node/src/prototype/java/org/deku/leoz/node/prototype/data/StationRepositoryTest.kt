package org.deku.leoz.node.prototype.data

import org.deku.leoz.node.Application
import org.deku.leoz.node.data.repository.master.StationRepository
import org.deku.leoz.node.test.config.ApplicationTestConfiguration
import org.deku.leoz.node.test.config.DataTestConfiguration
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import sx.junit.StandardTest
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
    fun test() {
        for (d in stationRepository.findAll()) {
            println(d)
        }
    }
}
