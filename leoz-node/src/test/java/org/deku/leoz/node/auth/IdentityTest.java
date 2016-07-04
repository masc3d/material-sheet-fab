package org.deku.leoz.node.auth;

import org.apache.commons.logging.Log;
import org.deku.leoz.Identity;
import org.deku.leoz.SystemInformation;
import org.deku.leoz.bundle.Bundles;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Created by masc on 27.06.15.
 */
public class IdentityTest {
    Log mLog = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testIdentity() {
        Identity ident = Identity.Companion.create(Bundles.LEOZ_NODE.getValue(), SystemInformation.create());
        mLog.info(ident);
    }
}
