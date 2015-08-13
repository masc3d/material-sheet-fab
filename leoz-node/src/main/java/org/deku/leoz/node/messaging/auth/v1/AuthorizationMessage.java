package org.deku.leoz.node.messaging.auth.v1;

import java.io.Serializable;

/**
 * Authorization message retrieved by node clients.
 * Created by masc on 30.06.15.
 */
public class AuthorizationMessage implements Serializable {
    private static final long serialVersionUID = 941655435886909768L;

    Integer mId;
    String mKey;
    Boolean mAuthorized;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public Boolean getAuthorized() {
        return mAuthorized;
    }

    public void setAuthorized(Boolean authorized) {
        mAuthorized = authorized;
    }

    @Override
    public String toString() {
        return String.format("Authorization id [%s] key [%s] authorized [%s]",
                mId,
                mKey,
                mAuthorized);
    }
}
