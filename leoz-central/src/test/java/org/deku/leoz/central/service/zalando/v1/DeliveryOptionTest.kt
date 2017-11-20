package org.deku.leoz.central.service.zalando.v1

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import org.deku.leoz.central.service.zalando.CarrierIntegrationService
import org.deku.leoz.node.rest.ObjectMapperProvider
import org.deku.leoz.service.zalando.entity.DeliveryOption
import org.junit.Assert
import org.junit.Ignore
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
class DeliveryOptionTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var objectMapperProviewr: ObjectMapperProvider

    private val objectMapper by lazy {
        val mapper = objectMapperProviewr.getContext(ObjectMapper::class.java)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    @Ignore
    @Test
    fun testDeliveryOption() {
        val o = DeliveryOption("test", Date(), Date(), Date(), Date())
        val oJson = this.objectMapper.writeValueAsString(o)
        log.info(oJson)
    }

    /**
     * Ensures that the serialized output date format uses UTC timezone with specialized short notation (Z)
     * as Zalando currnetly cannot handle zoned timestamps.
     */
    @Test
    fun testSerializedTimestamp() {
        val d = Date()
        val deliveryOption = DeliveryOption("test", d, d, d, d)

        val oJson = this.objectMapper.writeValueAsString(deliveryOption)
        val jnode: JsonNode = this.objectMapper.readTree(oJson)
        val jDateString = jnode.get("cut_off").textValue()

        Assert.assertEquals(jDateString, ISO8601DateFormat().format(d))
    }

    // TODO test freezes
    @Test
    @Ignore
    fun testGenerateDeliveryOptions() {
        val d = Date()
        val deliveryOption = DeliveryOption("228", d, d, d, d)
        val delOptions: List<DeliveryOption> = CarrierIntegrationService().generateDeliveryOptions(deliveryOption)
        assert(delOptions.size == 9)
    }
}