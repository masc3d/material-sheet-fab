package org.deku.leoz.node.test.config

import org.deku.leoz.node.Application
import org.deku.leoz.node.config.ExecutorConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.junit.runner.RunWith
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Created by masc on 12/03/2017.
 */
@Configuration
@Import(ApplicationTestConfiguration::class,
        PersistenceConfiguration::class,
        ExecutorConfiguration::class)
open class DataTestConfiguration