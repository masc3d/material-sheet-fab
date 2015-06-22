package org.deku.leo2.node.data.sync.v1;

import java.io.Serializable;

/**
 * Message sent by publishers prior to the actual entity payload, containing meta information
 * eg. amount of entites to expect
 * Created by masc on 18.06.15.
 */
public class EntityUpdateMessage implements Serializable {
    private static final long serialVersionUID = -8032738544698874536L;

    public static final String EOS_PROPERTY = "eos";

    private Long mAmount;

    public EntityUpdateMessage() { }

    /**
     * c'tor
     * @param amount Amount of records
     */
    public EntityUpdateMessage(Long amount) {
        mAmount = amount;
    }

    public Long getAmount() {
        return mAmount;
    }

    @Override
    public String toString() {
        return String.format("%s amount [%d]", this.getClass().getSimpleName(), this.getAmount());
    }
}
