package org.deku.leo2.node.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.inject.Named;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Customized object mapper provider
 * Created by masc on 21.04.15.
 */
@Named
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    private ObjectMapper mMapper;

    public ObjectMapperProvider() {
        mMapper = new ObjectMapper();
        // Don't epxlicitly serialize nulls with json (breaks swagger-ui too when having all those nulls in swagger.json)
        mMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Write date/times in JSON notation instead of (numeric) timestamps or arrays
        //mMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Read/Write enums using index
        mMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        mMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        // Enable support for java.time (java 8)
        //mMapper.registerModule(new JSR310Module());
    }
    @Override
    public ObjectMapper getContext(Class<?> type) {
        // Individual mapping per class, if required
        return mMapper;
    }
}
