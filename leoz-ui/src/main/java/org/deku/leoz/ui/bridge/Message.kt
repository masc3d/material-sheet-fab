package org.deku.leoz.ui.bridge

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.DateSerializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import java.io.IOException
import java.util.*

/**
 * LeoBridge message
 * Created by masc on 21.10.14.
 */
@JsonDeserialize(using = Message.JsonDeserializer::class)
@JsonSerialize(using = Message.JsonSerializer::class)
class Message {

    /**
     * JSON Message deserializer
     */
    class JsonDeserializer : com.fasterxml.jackson.databind.JsonDeserializer<Message>() {
        @Throws(IOException::class)
        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Message {
            var key = ""
            val attributes: HashMap<Any, Any> = HashMap()
            while (jp.hasCurrentToken()) {
                val jt = jp.currentToken
                if (jt.id() == JsonTokenId.ID_FIELD_NAME) {
                    key = jp.currentName
                }
                if (jt.isScalarValue) {
                    val value: Any
                    if (!jt.isNumeric && !jt.isBoolean) {
                        value = try {
                            jp.readValueAs(Date::class.java)
                        } catch (e: Exception) {
                            jp.readValueAs(Any::class.java)
                        }

                    } else {
                        value = jp.readValueAs(Any::class.java)
                    }
                    attributes.put(key, value)
                }
                jp.nextToken()
            }

            return Message(attributes)
        }
    }

    /**
     * JSON Message serializer
     */
    class JsonSerializer : com.fasterxml.jackson.databind.JsonSerializer<Message>() {
        @Throws(IOException::class)
        override fun serialize(value: Message, jgen: JsonGenerator, provider: SerializerProvider) {
            jgen.writeStartObject()
            for (entry in value.attributes.entries) {

                if (entry.value is Date) {
                    jgen.writeFieldName(entry.key.toString())
                    val ds = DateSerializer(false, StdDateFormat.getISO8601Format(StdDateFormat.getDefaultTimeZone(), Locale.getDefault()))
                    ds.serialize(entry.value as Date, jgen, provider)
                } else {
                    jgen.writeObjectField(entry.key.toString(), entry.value)
                }
            }
            jgen.writeEndObject()
        }
    }

    private var attributes: HashMap<Any, Any>

    /**
     * c'tor
     */
    constructor() {
        this.attributes = HashMap()
    }

    constructor(value: Any) : this() {
        this.attributes.put(DEFAULT_KEY, value)
    }

    constructor(attributes: HashMap<Any, Any>) {
        this.attributes = attributes
    }

    /**
     * Add message parameter
     * @param key
     * *
     * @param value
     */
    fun put(key: String, value: Any) {
        this.attributes.put(key, value)
    }

    /**
     * Get message parameter
     * @param key
     * *
     * @return
     */
    operator fun get(key: String): Any? {
        return attributes[key]
    }

    /**
     * Get default message parameter
     * @return
     */
    fun get(): Any? {
        return attributes[DEFAULT_KEY]
    }

    override fun toString(): String {
        var message = ""
        for (entry in attributes.entries) {
            if (message.isNotEmpty())
                message += ", "
            message += String.format("%s:%s", entry.key, entry.value)
        }
        return message
    }

    companion object {
        private val DEFAULT_KEY = "_"
    }
}
