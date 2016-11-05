package org.deku.leoz.central.data

import org.deku.leoz.central.DataTest
import org.deku.leoz.central.data.jooq.tables.records.MstNodeRecord
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by masc on 02.07.15.
 */
class NodeJooqRepositoryTest : DataTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Test
    fun testNodeJooqRepository() {
        val r = nodeJooqRepository.findByKeyOrCreateNew("test2")

        log.info(String.format("Node id [%d]", r.nodeId))
    }
}
