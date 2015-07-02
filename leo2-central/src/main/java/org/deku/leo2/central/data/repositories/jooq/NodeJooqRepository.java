package org.deku.leo2.central.data.repositories.jooq;

import org.deku.leo2.central.data.entities.jooq.Tables;
import org.deku.leo2.central.data.entities.jooq.tables.MstNode;
import org.deku.leo2.central.data.entities.jooq.tables.records.MstNodeRecord;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by masc on 02.07.15.
 */
@Named
public class NodeJooqRepository {
    @Inject
    DSLContext mDSLContext;

    /**
     * Find node record by key
     * @param key Key
     * @return Node record
     */
    public MstNodeRecord findByKey(String key) {
        return mDSLContext.fetchOne(MstNode.MST_NODE, Tables.MST_NODE.KEY.eq(key));
    }

    /**
     * Save record by key
     * @param key
     * @param hardwareAddress
     * @param systemInfo
     */
    public MstNodeRecord saveByKey(String key, String hardwareAddress, String systemInfo) {
        MstNodeRecord r = this.findByKey(key);
        if (r == null)
            r = mDSLContext.newRecord(Tables.MST_NODE);

        r.setKey(key);
        r.setHostname(hardwareAddress);
        r.setSysInfo(systemInfo);
        r.store();

        return r;
    }
}
