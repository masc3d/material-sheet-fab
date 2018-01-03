package org.deku.leoz.node.config

import org.deku.leoz.smartlane.SmartlaneBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * Created by masc on 30.12.17.
 */
@Configuration
@Lazy(false)
open class SmartlaneConfiguration {

    @get:Bean
    open val smartlaneBridge: SmartlaneBridge
        get() = SmartlaneBridge()
}