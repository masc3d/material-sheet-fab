package org.deku.leoz.node.auth;

import org.deku.leoz.Identity;
import org.deku.leoz.SystemInformation;
import org.deku.leoz.bundle.BundleType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by masc on 27.06.15.
 */
public class IdentityTest {
    Logger mLog = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testIdentity() {
        Identity ident = Identity.Companion.create(BundleType.LEOZ_NODE.getValue(), SystemInformation.create());
        mLog.info(ident.toString());
    }
}
