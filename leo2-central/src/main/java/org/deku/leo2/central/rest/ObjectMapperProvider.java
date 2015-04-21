package org.deku.leo2.central.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Customized object mapper provider, adding support for Java 8/JSR-310 java.time and other custom types
 * Created by masc on 21.04.15.
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    private ObjectMapper mMapper;

    public ObjectMapperProvider() {
        mMapper = new ObjectMapper();
        // Write date/times in JSON notation instead of (numeric) timestamps or arrays
        mMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mMapper.registerModule(new JSR310Module());
    }
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mMapper;
    }
}
