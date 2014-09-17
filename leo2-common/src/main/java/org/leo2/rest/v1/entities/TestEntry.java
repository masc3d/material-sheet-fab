package org.leo2.rest.v1.entities;

import java.util.Date;

/**
 * Created by masc on 23.07.14.
 */
public class TestEntry {
    public String name;
    public Date updated;

    public TestEntry() {
    }

    public TestEntry(String name) {
        this.name = name;
        this.updated = new Date();
    }

    @Override
    public String toString() {
        return String.format("Entry name [%s] updated [%s]", this.name, this.updated.toString());
    }
}
