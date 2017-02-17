package sx.maps

import java.lang.Boolean
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property delegate which extracts string value from a map and converts it to the property's type
 */
class MapValue<T>(
        private val map: Map<String, Any>,
        private val default: T,
        private val type: Class<T>)
    :
        ReadOnlyProperty<Any, T> {

    /**
     * Getter
     */
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return this.map[property.name.toLowerCase()] as? T ?: default
    }
}

inline fun <reified T : Any> mapValue(map: Map<String, Any>, default: T) = MapValue<T>(map, default, T::class.java)

/**
 * Extension method for conveniently navigating a tree of nested string based maps
 */
fun Map<String, Any>.resolve(name: String): Map<String, Any> {
    @Suppress("UNCHECKED_CAST")
    return this[name] as? Map<String, Any> ?: mapOf()
}