package org.deku.leoz

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.nodes.MappingNode
import org.yaml.snakeyaml.nodes.NodeTuple
import org.yaml.snakeyaml.nodes.Tag
import java.io.*

/**
 * Leoz file persistence. Serializes and deserializes objects using a common format.
 * Created by masc on 08/03/16.
 */
object YamlPersistence {

    private class Representer(
            val skipNulls: Boolean = false,
            val skipTags: Boolean = false)
        :
            org.yaml.snakeyaml.representer.Representer() {
        /***/
        override fun representJavaBeanProperty(javaBean: Any,
                                               property: Property,
                                               propertyValue: Any?,
                                               customTag: Tag?): NodeTuple? {

            if (skipTags && !classTags.containsKey(javaBean.javaClass))
                this.addClassTag(javaBean.javaClass, org.yaml.snakeyaml.nodes.Tag.MAP)

            if (skipNulls && propertyValue == null)
                return null

            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag)
        }


        override fun representJavaBean(properties: MutableSet<Property>?, javaBean: Any): MappingNode {
            if (skipTags && !classTags.containsKey(javaBean.javaClass))
                this.addClassTag(javaBean.javaClass, org.yaml.snakeyaml.nodes.Tag.MAP)

            val mappingNode = super.representJavaBean(properties, javaBean)
            return mappingNode
        }
    }

    /**
     * Yaml factory method
     */
    fun createYaml(representer: org.yaml.snakeyaml.representer.Representer = Representer()): Yaml {
        val options = DumperOptions()
        // Configure YAML so it's compatible with JSON
        options.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
        options.isPrettyFlow = true

        return Yaml(representer, options)
    }

    /**
     * Save object
     * @param obj Object to dump
     * @param file File to write to
     */
    fun save(obj: Any, skipNulls: Boolean = false, skipTags: Boolean = true, toFile: File) {
        OutputStreamWriter(FileOutputStream(toFile)).use {
            val y = this.createYaml(org.deku.leoz.YamlPersistence.Representer(skipNulls = skipNulls, skipTags = skipTags))
            it.write(y.dumpAs(obj, Tag.MAP, DumperOptions.FlowStyle.FLOW))
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
     * Load object from inputstream
     * @param type Class
     * @param reader Reader
     */
    fun <T> load(type: Class<T>, inputStream: InputStream): T {
        val y = this.createYaml()
        return y.loadAs(inputStream, type)
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
