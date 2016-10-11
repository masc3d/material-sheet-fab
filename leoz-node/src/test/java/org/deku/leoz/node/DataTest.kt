package org.deku.leoz.node

import org.deku.leoz.node.config.ExecutorConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.junit.Before
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableLoadTimeWeaving
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.servlet.ServletContext

/**
 * Created by masc on 15.05.15.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(
        org.deku.leoz.node.config.PersistenceConfiguration::class,
        FlywayAutoConfiguration::class,
        ExecutorConfiguration::class
))
@EnableConfigurationProperties
open class DataTest {

}
