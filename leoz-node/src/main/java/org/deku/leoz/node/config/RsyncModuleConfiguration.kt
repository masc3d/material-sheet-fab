package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sx.rsync.Rsync
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 10-Nov-15.
 */
@Named
@Configuration(RsyncModuleConfiguration.QUALIFIER)
open class RsyncModuleConfiguration {

    companion object {
        const val QUALIFIER = "RsyncModuleConfigurationNode"
    }

    @Inject
    private lateinit var storage: Storage

    @get:Bean
    open val bundlesModule: Rsync.Module
        get() {
            // Create bundle entry directories
            BundleType.values()
                    .map { storage.bundleRepositoryDirectory.resolve(it.value) }
                    .forEach { it.mkdirs() }

            return Rsync.Module(
                    org.deku.leoz.config.RsyncConfiguration.ModuleNames.BUNDLES,
                    storage.bundleRepositoryDirectory)
        }
}