package org.deku.leoz

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.deku.leoz.identity.Identity
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import sx.junit.StandardTest
import java.io.StringWriter

/**
 * Common object serialization tests
 * Created by masc on 27/10/2016.
 */
@Category(StandardTest::class)
class YmlJsonCompatibilityTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * State which may be persisted
     */
    data class State(
            var key: String = "Test123",
            var name: String = "Test123") {

        constructor(identity: Identity) : this(identity.uid.value, identity.name) {
        }
    }

    /**
     * Yaml
     */
    private val yaml by lazy {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
        options.isPrettyFlow = true
        Yaml(options)
    }

    /**
     * JSON object mapper (jackson)
     */
    private val jsonObjectMapper by lazy {
        val om = ObjectMapper()
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.setDefaultPrettyPrinter(
                DefaultPrettyPrinter().withObjectIndenter(
                        DefaultPrettyPrinter.FixedSpaceIndenter()))
        om
    }

    /**
     * Serialize object to yaml
     */
    private fun serializeToYsml(obj: Any): String {
        val sw = StringWriter()
        sw.use {
            this.yaml.dump(State(), it)
        }
        return sw.toString()
    }

    private fun serializeToJson(obj: Any): String {
        val sw = StringWriter()
        sw.use {
            this.jsonObjectMapper.writeValue(it, State())
        }
        return sw.toString()
    }

    @Test
    fun testYamlSerialization() {
        log.info(this.serializeToYsml(State()))
    }

    @Test
    fun testJsonSerialization() {
        log.info(this.serializeToJson(State()))
    }

    @Test
    fun testJsonToYamlSerializationCompatibility() {
        val a = State()

        val json = this.serializeToJson(a)
        val b = this.yaml.loadAs(json, State::class.java)

        Assert.assertEquals(a, b)
    }
}