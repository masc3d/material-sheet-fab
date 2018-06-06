package org.deku.leoz.central.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/**
 * Parcel service configuration
 * Created by JT on 01.09.17.
 */
@Component
class ParcelServiceConfiguration {

    /** Parcel service settings */
    @Configuration
    @ConfigurationProperties("service.parcel-service")
    class Settings {
        var skipParcelProcessing: Boolean = false
        var enableMessageEventCorrection: Boolean = false
    }
}