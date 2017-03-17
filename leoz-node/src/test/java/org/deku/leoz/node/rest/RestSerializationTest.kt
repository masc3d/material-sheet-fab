package org.deku.leoz.node.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import org.deku.leoz.node.rest.ObjectMapperProvider
import org.deku.leoz.node.rest.service.v1.RoutingService
import org.deku.leoz.node.config.DataTestConfiguration
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import java.time.LocalDateTime
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
        val i = ISO8601DateFormat().parse(oJson.trim('"'))
        Assert.assertEquals(o, i)
    }

    @Test
    fun testIso8601DateFormatParsing() {
        val t = "2016-11-28T10:34:25.097Z"
        val i = ISO8601DateFormat().parse(t)
        log.info("${i}")
    }

    @Test
    fun testIso8601Time() {
        val t = "17:00"
        val d = ISO8601DateFormat().parse(t)
        log.info("${d}")
    }
}