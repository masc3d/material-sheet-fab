package sx

import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Annotation for delcaring a configuration map path. Uses property/package notation. eg. 'server.services.rest'
 */
annotation class ConfigurationMapPath(val path: String)

/**
 * Configuration map base class.
 * Allows convenient mapping of properties via delegation to a structured configuration map which
 * can be created from eg. YAML.
 * Created by masc on 15/02/2017.
 */
abstract class ConfigurationMap(vararg sources: InputStream) {
    val map: Map<String, Any>

    init {
        this.map = this.normalize(
                this.loadMap(sources.toList()))
    }

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

    abstract fun loadMap(sources: List<InputStream>): Map<String, Any>

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

/**
 * Yaml configuration map
 * Created by masc on 15/02/2017.
 */
@Suppress("UNCHECKED_CAST")
class YamlConfigurationMap(vararg sources: InputStream) : ConfigurationMap(*sources) {

    override fun loadMap(sources: List<InputStream>): Map<String, Any> {
        val yaml = Yaml()
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
                if (it.value is Map <*, *>) {
                    val targetItem = target[it.key]
                    // If both source and target item are map
                    if (targetItem != null && targetItem is Map<*, *>) {
                        // Transform to mutable map and do recursive merge
                        val mutableTargetMap = targetItem.toMutableMap()
                        target[it.key] = mutableTargetMap
                        merge(
                                target = mutableTargetMap as MutableMap<String, Any>,
                                source = it.value as Map <String, Any>)

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
                    source = yaml.load(it) as Map<String, Any>)
        }

        return treeMap
    }
}