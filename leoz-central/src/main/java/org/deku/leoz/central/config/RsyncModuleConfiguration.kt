package org.deku.leoz.central.config

import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sx.rsync.Rsync
import javax.inject.Inject
import javax.inject.Named

/**
 * Leoz-central rsync module configuration.
 * Rsync modules are consumed by the @link RsyncServerConfiguration (leoz-node)
 * Created by masc on 10-Nov-15.
 */
@Named
@Configuration(RsyncModuleConfiguration.QUALIFIER)
open class RsyncModuleConfiguration {


    @Inject
    private lateinit var storage: Storage

    companion object {
        const val QUALIFIER = "RsyncModuleConfigurationCentral"
    }

    @get:Bean
    open val transferModule: Rsync.Module
        get() = Rsync.Module(
                RsyncConfiguration.ModuleNames.TRANSFER,
                storage.transferDirectory)
}