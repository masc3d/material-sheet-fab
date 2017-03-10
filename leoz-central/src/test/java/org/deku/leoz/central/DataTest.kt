package org.deku.leoz.central

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.central.config.ApplicationConfiguration
import org.deku.leoz.node.config.ExecutorConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.deku.leoz.node.config.StorageConfiguration
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
@RunWith(SpringJUnit4ClassRunner::class)
@ActiveProfiles(Application.PROFILE_CENTRAL)
@SpringBootTest(classes = arrayOf(
        org.deku.leoz.node.config.PersistenceConfiguration::class,
        org.deku.leoz.central.config.PersistenceConfiguration::class,
        ApplicationConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class))
@EnableConfigurationProperties
open class DataTest {
    companion object {
        init {
            Kodein.global.addImport(ApplicationConfiguration.module)
            Kodein.global.addImport(LogConfiguration.module)
            Kodein.global.addImport(StorageConfiguration.module)
            Kodein.global.instance<Application>().initialize()
        }
    }
}
