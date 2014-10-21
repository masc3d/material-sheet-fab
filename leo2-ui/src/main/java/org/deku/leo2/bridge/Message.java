package org.deku.leo2.bridge;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.io.IOException;
import java.util.*;

/**
 * LeoBridge message
 * Created by masc on 21.10.14.
 */
@JsonDeserialize(using = Message.JsonDeserializer.class)
@JsonSerialize(using = Message.JsonSerializer.class)
public class Message {
    private static final String DEFAULT_KEY = "_";

    /**
     * JSON Message deserializer
     */
    public static final class JsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Message> {
        @Override
        public Message deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String key = "";
            HashMap attributes = new HashMap();
            while(jp.hasCurrentToken()) {
                JsonToken jt = jp.getCurrentToken();
                if (jt.id() == JsonTokenId.ID_FIELD_NAME) {
                    key = jp.getCurrentName();
                }
                if (jt.isScalarValue()) {
                    Object value;
                    if (!jt.isNumeric() && !jt.isBoolean()) {
                        try {
                            value = jp.readValueAs(Date.class);
                        } catch(Exception e) {
                            value = jp.readValueAs(Object.class);
                        }
                    } else {
                        value = jp.readValueAs(Object.class);
                    }
                    attributes.put(key, value);
                }
                jp.nextToken();
            }

            return new Message(attributes);
        }
    }

    /**
     * JSON Message serializer
     */
    public static final class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Message> {
        @Override
        public void serialize(Message value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            for (Map.Entry<Object, Object> entry : value.mAttributes.entrySet()) {

                if (entry.getValue() instanceof Date) {
                    jgen.writeFieldName(entry.getKey().toString());
                    DateSerializer ds = new DateSerializer(false, StdDateFormat.getISO8601Format(StdDateFormat.getDefaultTimeZone()));
                    ds.serialize((Date)entry.getValue(), jgen, provider);
                } else {
                    jgen.writeObjectField(entry.getKey().toString(), entry.getValue());
                }
            }
            jgen.writeEndObject();
        }
    }

    private HashMap<Object, Object> mAttributes;

    /**
     * c'tor
     */
    public Message() {
        mAttributes = new HashMap();
    }

    public Message(Object value) {
        this();
        mAttributes.put(DEFAULT_KEY, value);
    }

    public Message(HashMap attributes) {
        mAttributes = attributes;
    }

    /**
     * Add message parameter
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        mAttributes.put(key, value);
    }

    /**
     * Get message parameter
     * @param key
     * @return
     */
    public Object get(String key) {
        return mAttributes.get(key);
    }

    /**
     * Get default message parameter
     * @return
     */
    public Object get() { return mAttributes.get(DEFAULT_KEY); }

    @Override
    public String toString() {
        String message = "";
        for (Map.Entry<Object, Object> entry : mAttributes.entrySet()) {
            if (message.length() > 0)
                message += ", ";
            message += String.format("%s:%s", entry.getKey(), entry.getValue());
        }
        return message;
    }
}
