package org.deku.leoz.node.data

import org.deku.leoz.node.DataTest
import org.deku.leoz.node.data.entities.QMstRoute
import org.deku.leoz.node.data.repositories.master.RouteRepository
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by masc on 07/10/2016.
 */
class RouteRepositoryTest : DataTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var routeRepository: RouteRepository

    @Test
    fun testFindAll() {
        routeRepository.findAll(QMstRoute.mstRoute.timestamp.gt(GregorianCalendar(2014, 1, 1).time)).forEach {
            log.info("${it} ${it.station}")
            return
        }
    }
}