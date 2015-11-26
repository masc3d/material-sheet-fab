package org.deku.leoz.node.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sx.rsync.Rsync
import javax.inject.Named

/**
 * Created by n3 on 10-Nov-15.
 */
@Named
@Configuration(RsyncModuleConfiguration.QUALIFIER)
open class RsyncModuleConfiguration {

    companion object {
        const val QUALIFIER = "RsyncModuleConfigurationNode"
    }

    @Bean
    open fun bundlesModule(): Rsync.Module {
        return Rsync.Module(
                org.deku.leoz.config.RsyncConfiguration.ModuleNames.BUNDLES,
                StorageConfiguration.instance.bundleRepositoryDirectory)
    }
}