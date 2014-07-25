package org.leo2.rest.v1;

import java.util.Date;

/**
 * Created by n3 on 23.07.14.
 */
public class TestEntry {
    public String name;
    public Date date;

    public TestEntry(String name) {
        this.name = name;
        this.date = new Date();
    }
}
