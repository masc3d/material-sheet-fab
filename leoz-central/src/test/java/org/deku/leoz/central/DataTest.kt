package org.deku.leoz.central

import org.deku.leoz.node.config.ExecutorConfiguration
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(
        org.deku.leoz.node.config.PersistenceConfiguration::class,
        org.deku.leoz.central.config.PersistenceConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class))
@EnableConfigurationProperties
open class DataTest {
    companion object {
        init {
            org.deku.leoz.node.Application.injectableInstance.set({ Application.instance })
            org.deku.leoz.node.Application.instance.initialize()
        }
    }
}
