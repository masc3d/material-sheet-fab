package org.deku.leo2.rest.v1;

import org.deku.leo2.rest.WebserviceTest;
import org.deku.leo2.rest.v1.entities.TestEntry;
import org.junit.Ignore;
import org.junit.Test;

public class TestServiceTest extends WebserviceTest {
    @Test
    public void testGet() throws Exception {

    }

    @Test
    @Ignore
    public void testGetPerformance() throws Exception {
        ITestService ts = this.getService(ITestService.class);

        for (int i = 0; i < 20; i++) {
            TestEntry[] entries = ts.get();

            for (TestEntry e : entries) {
                System.out.println(String.format("%d %s", i, e.toString()));
            }
        }
    }
}