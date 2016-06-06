package org.deku.leoz.bridge

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.DateSerializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import org.omg.CORBA.Object

import java.io.IOException
import java.util.Date
import java.util.HashMap
import java.util.Locale

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
                        try {
                            value = jp.readValueAs(Date::class.java)
                        } catch (e: Exception) {
                            value = jp.readValueAs(Any::class.java)
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
            for (entry in value.mAttributes!!.entries) {

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

    private var mAttributes: HashMap<Any, Any>? = null

    /**
     * c'tor
     */
    constructor() {
        mAttributes = HashMap()
    }

    constructor(value: Any) : this() {
        mAttributes!!.put(DEFAULT_KEY, value)
    }

    constructor(attributes: HashMap<Any, Any>) {
        mAttributes = attributes
    }

    /**
     * Add message parameter
     * @param key
     * *
     * @param value
     */
    fun put(key: String, value: Any) {
        mAttributes!!.put(key, value)
    }

    /**
     * Get message parameter
     * @param key
     * *
     * @return
     */
    operator fun get(key: String): Any? {
        return mAttributes!![key]
    }

    /**
     * Get default message parameter
     * @return
     */
    fun get(): Any? {
        return mAttributes!![DEFAULT_KEY]
    }

    override fun toString(): String {
        var message = ""
        for (entry in mAttributes!!.entries) {
            if (message.length > 0)
                message += ", "
            message += String.format("%s:%s", entry.key, entry.value)
        }
        return message
    }

    companion object {
        private val DEFAULT_KEY = "_"
    }
}
