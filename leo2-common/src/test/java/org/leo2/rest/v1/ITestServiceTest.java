package org.leo2.rest.v1;

import org.junit.*;
import org.leo2.rest.WebserviceTest;

import static org.junit.Assert.*;

public class ITestServiceTest extends WebserviceTest {
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