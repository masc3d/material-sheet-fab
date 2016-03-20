package org.deku.leoz.central.config

import org.deku.leoz.config.RsyncConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sx.rsync.Rsync
import javax.inject.Named

/**
 * Leoz-central rsync module configuration.
 * Rsync modules are consumed by the @link RsyncServerConfiguration (leoz-node)
 * Created by masc on 10-Nov-15.
 */
@Named
@Configuration(RsyncModuleConfiguration.QUALIFIER)
open class RsyncModuleConfiguration {

    companion object {
        const val QUALIFIER = "RsyncModuleConfigurationCentral"
    }

    @Bean
    open fun transferModule(): Rsync.Module {
        return Rsync.Module(
                RsyncConfiguration.ModuleNames.TRANSFER,
                StorageConfiguration.instance.transferDirectory)
    }
}