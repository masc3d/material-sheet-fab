package org.deku.leoz.central.data

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.DatabaseSyncConfiguration
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest

import javax.inject.Inject

/**
 * Created by masc on 02.07.15.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        DatabaseSyncConfiguration::class,
        DatabaseSyncService::class
))
class NodeJooqRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Test
    fun testNodeJooqRepository() {
        val r = nodeJooqRepository.findByKeyOrCreateNew("test2")

        log.info(String.format("Node id [%d]", r.nodeId))
    }
}
