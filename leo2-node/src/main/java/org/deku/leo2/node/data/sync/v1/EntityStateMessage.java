package org.deku.leo2.node.data.sync.v1;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Message sent by
 * - consumers to request entity updates
 * - publishers to indicate entity updates
 * Created by masc on 18.06.15.
 */
public class EntityStateMessage implements Serializable {
    private static final long serialVersionUID = -3506609875846947166L;

    public static final String ENTITY_QUEUE_NAME = "leo2.entity.v1";

    private Class mEntityType;
    private ZonedDateTime mTimestamp;

    public EntityStateMessage() { }

    public EntityStateMessage(Class entityType, ZonedDateTime timestamp) {
        mEntityType = entityType;
        mTimestamp = timestamp;
    }

    public Class getEntityType() {
        return mEntityType;
    }

    public ZonedDateTime getTimestamp() {
        return mTimestamp;
    }
}
