package org.deku.leo2.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * Adapter for marshalling jsr-310 LocalDate from/to xml
 * Created by masc on 21.04.15.
 */
public class LocalDateAdapter
        extends XmlAdapter<String, LocalDate>{

    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v);
    }

    public String marshal(LocalDate v) throws Exception {
        return v.toString();
    }
}