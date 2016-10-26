package org.deku.leoz.node.test

import org.deku.leoz.node.config.ExecutorConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(
        PersistenceConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class
))
@EnableConfigurationProperties
open class DataTest {

}
