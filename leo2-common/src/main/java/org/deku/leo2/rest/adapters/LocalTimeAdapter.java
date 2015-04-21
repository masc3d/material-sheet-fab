package org.deku.leo2.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

/**
 * Adapter for marshalling jsr-310 LocalTime from/to xml
 * Created by masc on 21.04.15.
 */
public class LocalTimeAdapter
        extends XmlAdapter<String, LocalTime>{

    public LocalTime unmarshal(String v) throws Exception {
        return LocalTime.parse(v);
    }

    public String marshal(LocalTime v) throws Exception {
        return v.toString();
    }
}