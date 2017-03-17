package org.deku.leoz.node.rest

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

import javax.inject.Named
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

/**
 * Customized object mapper provider
 * Created by masc on 21.04.15.
 */
@Named
@Provider
class ObjectMapperProvider : ContextResolver<ObjectMapper> {
    private val mapper: ObjectMapper

    init {
        mapper = ObjectMapper()
        // Don't epxlicitly serialize nulls with json (breaks swagger-ui too when having all those nulls in swagger.json)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        // Write date/times in JSON notation instead of (numeric) timestamps or arrays
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        // Read/Write enums using index
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true)
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        // Enable support for java.time (java 8)
        //mMapper.registerModule(new JSR310Module());
    }

    override fun getContext(type: Class<*>): ObjectMapper {
        // Individual mapping per class, if required
        return mapper
    }
}
