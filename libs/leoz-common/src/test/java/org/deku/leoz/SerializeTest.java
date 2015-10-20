package org.deku.leoz;

import org.deku.leoz.log.LogMessage;
import org.junit.Assert;
import org.junit.Test;

import java.io.ObjectStreamClass;

/**
 * Created by masc on 12.10.15.
 */
public class SerializeTest {

    /**
     * Mainly for testing readability of serialUID of a kotlin class
     */
    @Test
    public void testUid() {
        Assert.assertEquals(ObjectStreamClass.lookup(LogMessage.class).getSerialVersionUID(), -8027400236775552276L);
    }
}
