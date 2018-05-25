package org.deku.leoz.node.service.internal

import org.deku.leoz.node.config.ApplicationTestConfiguration
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.service.pub.RoutingService
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import sx.junit.StandardTest
import sx.log.slf4j.trace
import javax.inject.Inject

/**
 * Created by masc on 25.05.18.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        ApplicationTestConfiguration::class,
        RecoveryService::class
))
class RecoveryServiceTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var recoveryService: RecoveryService

    @Test
    fun testRecoverMobileParcelMessages() {
        recoveryService.recoverMobileParcelMessages()
    }
}