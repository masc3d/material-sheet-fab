package org.deku.leoz.central.data

import org.deku.leoz.central.DataTest
import org.deku.leoz.central.data.entities.jooq.tables.records.MstNodeRecord
import org.deku.leoz.central.data.repositories.NodeRepository
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by masc on 02.07.15.
 */
class NodeRepositoryTest : DataTest() {
    private val mLog = LoggerFactory.getLogger(this.javaClass)

    @Inject
    internal var mNodeRepository: NodeRepository? = null

    @Test
    fun testNodeJooqRepository() {
        val r = mNodeRepository!!.findByKeyOrCreateNew("test2")

        mLog.info(String.format("Node id [%d]", r.nodeId))
    }
}
