package org.deku.leo2.central.rest.services.internal.v1;

import org.deku.leo2.rest.entities.internal.v1.TestEntry;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * Created by masc on 23.07.14.
 */
@Path("internal/v1/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestService implements org.deku.leo2.rest.services.internal.v1.TestService {
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
