package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.ApplicationTestConfiguration
import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.ParcelServiceConfiguration
import org.deku.leoz.node.service.pub.DocumentService
import org.deku.leoz.time.toShortDate
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import java.util.*
import javax.inject.Inject

/**
 * Created by masc on 25.05.18.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        RecoveryService::class,
        ParcelServiceV1::class,
        ParcelServiceConfiguration::class,
        ParcelProcessingService::class,
        DocumentService::class
))
class RecoveryServiceTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var recoveryService: RecoveryService

    @Test
    fun testRecoverMobileParcelMessages() {
        recoveryService.recoverMobileParcelMessages(
                dryRun = true,
                logDate = Date().toShortDate(),
                applicationUpdateVersion = "0.150"
        )
    }
}