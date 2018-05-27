package org.deku.leoz.node.config

import org.deku.leoz.node.config.PersistenceConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Created by masc on 12/03/2017.
 */
@Configuration
@Import(ApplicationTestConfiguration::class,
        PersistenceConfiguration::class,
        ExecutorConfiguration::class)
class DataTestConfiguration