package org.deku.leoz.node.test.config

import org.deku.leoz.node.Application
import org.deku.leoz.node.config.ExecutorConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by masc on 12/03/2017.
 */
@Configuration
@Import(ApplicationTestConfiguration::class,
        PersistenceConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class)
open class DataTestConfiguration {

}