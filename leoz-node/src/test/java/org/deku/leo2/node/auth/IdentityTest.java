package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * Created by masc on 27.06.15.
 */
public class IdentityTest {
    Log mLog = LogFactory.getLog(this.getClass());

    @Test
    public void testIdentity() {
        Identity ident = Identity.create(SystemInformation.create());
        mLog.info(ident);
    }
}
