package org.deku.leoz.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.Identity;
import org.deku.leoz.SystemInformation;
import org.junit.Test;

/**
 * Created by masc on 27.06.15.
 */
public class IdentityTest {
    Log mLog = LogFactory.getLog(this.getClass());

    @Test
    public void testIdentity() {
        Identity ident = Identity.Companion.create(SystemInformation.create());
        mLog.info(ident);
    }
}
