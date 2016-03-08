package org.deku.leoz

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * Leoz specific object serialization to/from files
 * Created by masc on 08/03/16.
 */
object FilePersistence {

    /**
     * Yaml factory method
     */
    private fun createYaml(): Yaml {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
        options.isPrettyFlow = true
        return Yaml(options)
    }

    /**
     * Dump object to file
     * @param obj Object to dump
     */
    fun dump(obj: Any, toFile: File) {
        throw NotImplementedError()
    }

    /**
     * Load object from file
     * @return Object
     */
    fun loadObject(fromFile: File): Any? {
        throw NotImplementedError()
    }

    /**
     * Dump map to file
     * @param map
     * @param toFile Destination file
     */
    fun dump(map: Map<String, Any>, toFile: File) {
        val y = this.createYaml()
        OutputStreamWriter(FileOutputStream(toFile)).use {
            y.dump(map, it)
        }
    }

    /**
     * Load map from file
     * @return Deserialized map
     */
    @Suppress("UNCHECKED_CAST")
    fun loadMap(fromFile: File): Map<String, Any> {
        val y = this.createYaml()

        var data: Map<String, Any> = mapOf()
        FileInputStream(fromFile).use {
            return y.load(it) as Map<String, Any>
        }
    }
}
