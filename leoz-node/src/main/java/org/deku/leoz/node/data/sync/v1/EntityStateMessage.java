package org.deku.leoz.node.data.sync.v1;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Message sent by
 * - consumers to request entity updates
 * - publishers to indicate entity updates
 * Created by masc on 18.06.15.
 */
public class EntityStateMessage implements Serializable {
    private static final long serialVersionUID = -3506609875846947166L;
    private Class mEntityType;
    private Timestamp mTimestamp;

    /**
     * c'tor
     * @param entityType
     * @param timestamp
     */
    public EntityStateMessage(Class entityType, Timestamp timestamp) {
        mEntityType = entityType;
        mTimestamp = timestamp;
    }
    public EntityStateMessage() { }

    /**
     * Entity type this state message refers to
     * @return
     */
    public Class getEntityType() {
        return mEntityType;
    }

    /**
     * Latest entity timestamp
     * @return
     */
    public Timestamp getTimestamp() {
        return mTimestamp;
    }
}
