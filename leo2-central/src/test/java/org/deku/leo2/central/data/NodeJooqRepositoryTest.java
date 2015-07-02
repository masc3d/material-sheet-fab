package org.deku.leo2.central.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.DataTest;
import org.deku.leo2.central.data.entities.jooq.tables.records.MstNodeRecord;
import org.deku.leo2.central.data.repositories.jooq.NodeJooqRepository;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by masc on 02.07.15.
 */
public class NodeJooqRepositoryTest extends DataTest {
    private Log mLog = LogFactory.getLog(this.getClass());

    @Inject
    NodeJooqRepository mNodeJooqRepository;

    @Test
    public void testNodeJooqRepository() {
        MstNodeRecord r = mNodeJooqRepository.saveByKey("test2", "555", "456");

        mLog.info(String.format("Node id [%d]", r.getNodeId()));
    }
}
