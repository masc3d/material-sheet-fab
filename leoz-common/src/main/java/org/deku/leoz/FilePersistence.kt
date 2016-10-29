package org.deku.leoz

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.*

/**
 * Leoz file persistence. Serializes and deserializes objects using a common format.
 * Created by masc on 08/03/16.
 */
object FilePersistence {
    /**
     * Yaml factory method
     */
    private fun createYaml(): Yaml {
        val options = DumperOptions()
        // Configure YAML so it's compatible with JSON
        options.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
        options.isPrettyFlow = true
        return Yaml(options)
    }

    /**
     * Save object
     * @param obj Object to save
     * @param writer Writer to use
     */
    fun save(obj: Any, writer: Writer) {
        val y = this.createYaml()
        y.dump(obj, writer)
    }

    /**
     * Save object
     * @param obj Object to dump
     * @param file File to write to
     */
    fun save(obj: Any, toFile: File) {
        OutputStreamWriter(FileOutputStream(toFile)).use {
            this.save(obj, it)
        }
    }

    /**
     * Load object from reader
     * @param type Class
     * @param reader Reader
     */
    fun <T> load(type: Class<T>, reader: Reader): T {
        val y = this.createYaml()
        return y.loadAs(reader, type)
    }

    /**
     * Load object from file
     * @param type Class
     * @param fromFile File to load from
     * @return Object
     */
    fun <T> load(type: Class<T>, fromFile: File): T {
        InputStreamReader(FileInputStream(fromFile)).use {
            return this.load(type, it)
        }
    }
}
