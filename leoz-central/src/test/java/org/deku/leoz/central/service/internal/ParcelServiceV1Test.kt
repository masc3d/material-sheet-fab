package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.ParcelServiceConfiguration
import org.deku.leoz.central.data.ParcelProcessing
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Created by masc on 13.06.17.
 */
@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [
    DataTestConfiguration::class,
    ParcelProcessing::class,
    ParcelServiceConfiguration::class,
    ParcelServiceConfiguration.Settings::class,
    ParcelServiceV1::class]
)
class ParcelServiceV1Test {
    @Inject
    lateinit var parcelService: org.deku.leoz.central.service.internal.ParcelServiceV1

    @Test
    fun testTranscodeSvg2Jpg() {
        val output = ByteArrayOutputStream()

        this.parcelService.transcodeSvg2Jpg(
                input = this.javaClass.getResourceAsStream("/test.svg"),
                output = output
        )
    }
}
