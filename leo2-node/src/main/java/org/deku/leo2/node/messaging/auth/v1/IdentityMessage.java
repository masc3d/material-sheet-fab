package org.deku.leo2.node.messaging.auth.v1;

import java.io.Serializable;

/**
 * Identity information message sent from node clients to central.
 * Created by masc on 30.06.15.
 */
public class IdentityMessage implements Serializable {
    private static final long serialVersionUID = -6588650210003644996L;

    Integer mId;
    String mKey;
    String mHardwareAddress;
    String mSystemInfo;

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

    public String getHardwareAddress() {
        return mHardwareAddress;
    }

    public void setHardwareAddress(String hardwareAddress) {
        mHardwareAddress = hardwareAddress;
    }

    public String getSystemInfo() {
        return mSystemInfo;
    }

    public void setSystemInfo(String systemInfo) {
        mSystemInfo = systemInfo;
    }
}
