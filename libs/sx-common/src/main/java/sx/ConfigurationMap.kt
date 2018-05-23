package sx

import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Annotation for delcaring a configuration map path. Uses property/package notation. eg. 'server.services.rest'
 */
annotation class ConfigurationMapPath(val path: String)

/**
 * Configuration map
 *
 * Allows convenient mapping of properties via delegation to a structured configuration map which
 * can be created from eg. YAML.
 *
 * Created by masc on 15/02/2017.
 */
class ConfigurationMap() {
    private var map: Map<String, Any> = mapOf()

    /**
     * Extension method for conveniently navigating a tree of nested string based maps
     */
    private fun Map<String, Any>.resolve(name: String): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return this[name] as? Map<String, Any> ?: mapOf()
    }

    /**
     * Recursively mangles all key names so they're in line with property names (removal of hyphens eg. etc.)
     */
    @Suppress("UNCHECKED_CAST")
    private fun normalize(sourceMap: Map<String, Any>): Map<String, Any> {
        return mapOf(*sourceMap.map {
            val newKey = it.key.toLowerCase().replace("-", "")

            when (it.value) {
                is Map<*, *> -> Pair(newKey, normalize(it.value as Map<String, Any>))
                else -> Pair(newKey, it.value)
            }
        }.toTypedArray())
    }

    /**
     * Set configuration map based on sources in overriding order
     * @param sources configuration map sources
     */
    fun set(sources: List<Map<String, Any>>) {
        val treeMap = mutableMapOf<String, Any>()

        /**
         * Merge maps recursively
         * @param target The target map to merge to. Items will be modified in place.
         * @param source The source map to merge
         */
        fun merge(
                target: MutableMap<String, Any>,
                source: Map<String, Any>): MutableMap<String, Any> {

            source.entries.forEach {
                if (it.value is Map<*, *>) {
                    val targetItem = target[it.key]
                    // If both source and target item are map
                    if (targetItem != null && targetItem is Map<*, *>) {
                        // Transform to mutable map and do recursive merge
                        val mutableTargetMap = targetItem.toMutableMap()
                        target[it.key] = mutableTargetMap

                        @Suppress("UNCHECKED_CAST")
                        merge(
                                target = mutableTargetMap as MutableMap<String, Any>,
                                source = it.value as Map<String, Any>)

                    } else {
                        target[it.key] = it.value
                    }
                } else {
                    target[it.key] = it.value
                }
            }

            return target
        }

        // Load overrides
        sources.forEach {
            merge(
                    target = treeMap,
                    source = it
            )
        }

        // TODO: consider to normalize during merge to additional recursive iteration
        this.map = this.normalize(treeMap)
    }

    /**
     * Property delegate which extracts string value from a map and converts it to the property's type
     */
    inner class MapValue<T>(
            private val default: T,
            private val type: Class<T>)
        :
            ReadOnlyProperty<Any, T> {

        /**
         * Getter
         */
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            // TODO: support caching

            val mapPath = thisRef.javaClass
                    .annotationOfType(ConfigurationMapPath::class.java)
                    .path
                    .toLowerCase()
                    .replace("-", "")

            var map: Map<String, Any> = this@ConfigurationMap.map
            mapPath.split(".").forEach {
                map = map.resolve(it)
            }

            return map[property.name.toLowerCase()] as? T ?: default
        }
    }

    inline fun <reified T : Any?> value(default: T) = MapValue<T>(default, T::class.java)
}
