package org.deku.leoz.node.rest

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

/**
 * Created by masc on 16/03/2017.
 */
@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        ObjectMapperProvider::class
))
class RestSerializationTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var objectMapperProviewr: ObjectMapperProvider

    private val objectMapper by lazy {
        val mapper = objectMapperProviewr.getContext(ObjectMapper::class.java)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    @Test
    fun testIso8601DateFormat() {
        val o = Date()
        val oJson = this.objectMapper.writeValueAsString(o)
        log.info(oJson)
        val i = StdDateFormat().parse(oJson.trim('"'))
        Assert.assertEquals(o, i)
    }

    @Test
    fun testIso8601ZoneDateFormat() {
        val o = ZonedDateTime.now()
        val oJson = this.objectMapper.writeValueAsString(o)
        log.info(oJson)
        val i = StdDateFormat().parse(oJson.trim('"'))
    }

    @Test
    fun testIso8601DateFormatParsing1() {
        val t = "2016-11-28T10:34:25.097Z"
        val i = StdDateFormat().parse(t)
        log.info("${i}")
    }

    @Test
    fun testIso8601DateFormatParsing2() {
        val t = "2017-03-28T15:00:00.000+0200"
        val i = StdDateFormat().parse(t)
        log.info("${i}")
    }

    enum class Enum1(
            val intValue: Int,
            @get:JsonValue
            val value: String) {
        TEST1(1, "test1"),
        TEST2(2, "test2")
    }

    @Test
    fun testSerializeEnum() {
        val o = Enum1.TEST1
        val oJson = this.objectMapper.writeValueAsString(o)
        Assert.assertEquals("\"${Enum1.TEST1.value}\"", oJson)

        val d = this.objectMapper.readValue(oJson, Enum1::class.java)
        Assert.assertEquals(d, Enum1.TEST1)
    }
}