package org.deku.leoz.node.prototype.data

import org.deku.leoz.node.test.DataTest
import org.deku.leoz.node.data.repository.master.StationRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by masc on 15.05.15.
 */
@Category(PrototypeTest::class)
class StationRepositoryTest : DataTest() {
    @Inject
    private lateinit var stationRepository: StationRepository

    @Test
    fun test() {
        for (d in stationRepository.findAll()) {
            println(d)
        }
    }
}
