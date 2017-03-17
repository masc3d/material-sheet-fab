package org.deku.leoz.central.rest.services.zalando.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.deku.leoz.node.rest.ObjectMapperProvider
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOption
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
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
    fun testDeliveryOption() {
        val o = DeliveryOption("test", Date(), Date(), Date(), Date())
        val oJson = this.objectMapper.writeValueAsString(o)
        log.info(oJson)
    }
}