package org.deku.leoz.central.config

import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import sx.rsync.Rsync
import javax.inject.Inject

/**
 * Leoz-central rsync module configuration.
 * Rsync modules are consumed by the @link RsyncServerConfiguration (leoz-node)
 * Created by masc on 10-Nov-15.
 */
@Component
@Configuration(RsyncModuleConfiguration.QUALIFIER)
class RsyncModuleConfiguration {


    @Inject
    private lateinit var storage: Storage

    companion object {
        const val QUALIFIER = "RsyncModuleConfigurationCentral"
    }

    @get:Bean
    val transferModule: Rsync.Module
        get() = Rsync.Module(
                RsyncConfiguration.ModuleNames.TRANSFER,
                storage.transferDirectory)
}