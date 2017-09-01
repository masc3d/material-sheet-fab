package org.deku.leoz.central.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import javax.inject.Named

/**
 * ParcelService configuration.
 * Created by JT on 01.09.17.
 */
@Named
open class ParcelMessageServiceConfiguration {

    open class ParcelMessageServiceConfiguration {
        var skipParcelProcessing: Boolean = false
    }

    @get:ConfigurationProperties("service.parcel-service")
    @get:Bean
    open val parcelMessageServiceConfiguration: ParcelMessageServiceConfiguration = ParcelMessageServiceConfiguration()

    @get:Bean
    open val doSkipParcelProcessing: Boolean
        get() {
// Test/debug option which skips effective parcel data processing
          return this.parcelMessageServiceConfiguration.skipParcelProcessing
        }
}