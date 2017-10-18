package org.deku.leoz.central.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.inject.Named

/**
 * Parcel service configuration
 * Created by JT on 01.09.17.
 */
@Named
open class ParcelServiceConfiguration {

    /** Parcel service settings */
    @Configuration
    @ConfigurationProperties("service.parcel-service")
    open class Settings {
        var skipParcelProcessing: Boolean = false
    }
}