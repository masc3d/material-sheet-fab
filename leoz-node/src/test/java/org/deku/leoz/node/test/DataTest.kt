package org.deku.leoz.node.test

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.deku.leoz.node.config.*
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(
        ApplicationConfiguration::class,
        PersistenceConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class
))
@EnableConfigurationProperties
@ActiveProfiles(Application.PROFILE_CLIENT_NODE)
open class DataTest {
    companion object {
        init {
            Kodein.global.addImport(ApplicationConfiguration.module)
            Kodein.global.addImport(StorageConfiguration.module)
            Kodein.global.addImport(LogConfiguration.module)
        }
    }
}
