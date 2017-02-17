package org.deku.leoz.mobile

import android.content.Context
import android.util.Log
import com.github.salomonbrys.kodein.TypeToken
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.YamlPersistence
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import sx.Stopwatch
import sx.maps.resolve
import java.io.FileNotFoundException
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Settings root container/map.
 * Can be used conveniently with delegated properties {@see #sx.maps.StringMapDelegates.kt} for property expansion.
 * Created by masc on 15/02/2017.
 */
open class Settings(
        private val context: Context) {

    @Suppress("UNCHECKED_CAST")
    val map: Map<String, Any> by lazy {
        val yaml = Yaml()
        val treeMap = mutableMapOf<String, Any>()

        // Load regular settings
        run {
            val inputStream = this.context.assets.open("application.yml")
            treeMap.putAll(yaml.load(inputStream) as Map<String, Any>)
        }

        // Load debug settings (overrides)
        if (BuildConfig.DEBUG) {
            try {
                val inputStream = this.context.assets.open("application-debug.yml")
                treeMap.putAll(yaml.load(inputStream) as Map<String, Any>)
            } catch(e: FileNotFoundException) {
                // No debug configuration, it's ok.
            }
        }

        /**
         * Recursively mangles all key names so they're in line with property names (removal of hyphens eg. etc.)
         */
        fun mangle(sourceMap: Map<String, Any>): Map<String, Any> {
            return mapOf(*sourceMap.map {
                val newKey = it.key.toLowerCase().replace("-", "")

                when (it.value) {
                    is Map<*,*> -> Pair(newKey, mangle(it.value as Map<String, Any>))
                    else -> Pair(newKey, it.value)
                }
            }.toTypedArray())
        }

        mangle(treeMap)
    }

    /**
     * Resolve nested tree (convenience method)
     */
    fun resolve(name: String): Map<String, Any> {
        return this.map.resolve(name)
    }
}
