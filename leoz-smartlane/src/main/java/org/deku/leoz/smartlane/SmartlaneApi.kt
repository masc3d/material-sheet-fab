package org.deku.leoz.smartlane

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.util.ISO8601DateFormat

/**
 * Created by masc on 20.11.17.
 */
class SmartlaneApi {
    companion object {
        val mapper = ObjectMapper().also {
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            it.setDateFormat(ISO8601DateFormat())
        }
    }
}