package org.deku.leo2.central.rest.v1;

import org.deku.leo2.central.rest.WebserviceTest;
import org.deku.leo2.entities.v1.TestEntry;
import org.deku.leo2.rest.v1.TestService;
import org.junit.Ignore;
import org.junit.Test;

public class TestServiceTest extends WebserviceTest {
    @Test
    public void testGet() throws Exception {

    }

    @Test
    @Ignore
    public void testGetPerformance() throws Exception {
        TestService ts = this.getService(TestService.class);

        for (int i = 0; i < 20; i++) {
            TestEntry[] entries = ts.get();

            for (TestEntry e : entries) {
                System.out.println(String.format("%d %s", i, e.toString()));
            }
        }
    }
}