package org.deku.leo2.central.data.repositories;

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
public class NodeRepository {
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
     * Check if there's a record for a specific key
     * @param key
     * @return
     */
    public boolean hasAuthorizedKey(String key) {
        MstNodeRecord r = this.findByKey(key);
        return r != null && r.getAuthorized() != 0;
    }

    /**
     * Find node record by key or create new one if there's none
     * @param key
     */
    public MstNodeRecord findByKeyOrCreateNew(String key) {
        MstNodeRecord r = this.findByKey(key);
        if (r == null)
            r = mDSLContext.newRecord(Tables.MST_NODE);
        return r;
    }
}
