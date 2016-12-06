package org.deku.leoz.central

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.central.config.ApplicationConfiguration
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
            Kodein.global.addImport(ApplicationConfiguration.module)

            Kodein.global.instance<Application>().initialize()
        }
    }
}
