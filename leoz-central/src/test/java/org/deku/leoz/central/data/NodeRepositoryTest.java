package org.deku.leoz.central.data;

import org.apache.commons.logging.Log;
import org.deku.leoz.central.DataTest;
import org.deku.leoz.central.data.entities.jooq.tables.records.MstNodeRecord;
import org.deku.leoz.central.data.repositories.NodeRepository;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by masc on 02.07.15.
 */
public class NodeRepositoryTest extends DataTest {
    private Log mLog = LoggerFactory.getLogger(this.getClass());

    @Inject
    NodeRepository mNodeRepository;

    @Test
    public void testNodeJooqRepository() {
        MstNodeRecord r = mNodeRepository.findByKeyOrCreateNew("test2");

        mLog.info(String.format("Node id [%d]", r.getNodeId()));
    }
}
