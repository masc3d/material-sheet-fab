package org.deku.leoz.node.data

import org.deku.leoz.node.DataTest
import org.deku.leoz.node.data.entities.master.Station
import org.deku.leoz.node.data.repositories.master.StationRepository
import org.junit.Test

import javax.inject.Inject

/**
 * Created by masc on 15.05.15.
 */
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
