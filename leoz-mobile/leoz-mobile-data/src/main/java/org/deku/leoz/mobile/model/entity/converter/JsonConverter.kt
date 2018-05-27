package org.deku.leoz.mobile.model.entity.converter

import io.requery.Converter
import sx.io.serialization.JacksonSerializer

/**
 * Requery json converter
 * Created by masc on 17.07.17.
 */
open class JsonConverter : Converter<Any, String> {

    companion object {
        private val serializers by lazy {
            object : ThreadLocal<JacksonSerializer>() {
                override fun initialValue(): JacksonSerializer = JacksonSerializer()
            }
        }
    }

    val serializer get() = serializers.get()

    override fun getMappedType(): Class<Any> = Any::class.java

    override fun getPersistedType(): Class<String> = String::class.java

    override fun getPersistedSize(): Int? = null

    override fun convertToMapped(type: Class<out Any>, value: String?): Any? {
        if (value == null || value.isEmpty())
            return null

        return this.serializer.deserialize((value.byteInputStream()))
    }

    override fun convertToPersisted(value: Any?): String {
        if (value == null)
            return ""

        return this.serializer.serializeToByteArray(value).toString(charset = Charsets.UTF_8)
    }
}