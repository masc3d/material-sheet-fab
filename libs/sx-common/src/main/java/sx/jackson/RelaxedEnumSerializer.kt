package sx.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

/**
 * Serializes unknown enum values to null
 * Created by masc on 27.06.17.
 */
class RelaxedEnumSerializer<E : Enum<E>>
    : JsonDeserializer<E>(), ContextualDeserializer {

    private lateinit var type: Class<E>

    private val enumValuesByName by lazy {
        mapOf(*this.type.enumConstants.map {
            Pair(it.name, it)
        }.toTypedArray())
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty): JsonDeserializer<*> {
        @Suppress("UNCHECKED_CAST")
        this.type = property.type.rawClass as Class<E>
        return this
    }


    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): E? {
        val value = p.readValueAs(String::class.java)
        return this.enumValuesByName.get(value)
    }
}