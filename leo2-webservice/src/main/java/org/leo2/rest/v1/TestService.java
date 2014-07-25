package org.leo2.rest.v1;

import org.leo2.rest.v1.ITestService;
import org.leo2.rest.v1.TestEntry;

import javax.ws.rs.Path;
import java.util.ArrayList;

/**
 * Created by masc on 23.07.14.
 */
@Path("/v1/test")
public class TestService implements ITestService {
    @Override
    public TestEntry[] get() {
        ArrayList<TestEntry> entries = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            entries.add(new TestEntry("test"));
        return entries.toArray(new TestEntry[0]);
    }

    @Override
    public TestEntry get(String name) {
        return null;
    }

    @Override
    public TestEntry[] find(String query) {
        return new TestEntry[0];
    }
}
