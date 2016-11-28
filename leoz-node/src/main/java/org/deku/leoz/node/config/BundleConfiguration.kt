package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.config.BundleConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.rsync.Rsync
import javax.inject.Inject

/**
 * Created by masc on 09/03/16.
 */
@Configuration
@Lazy(false)
open class BundleConfiguration {
    @Inject
    private lateinit var storageConfiguration: StorageConfiguration

    /**
     * Local bundle repository
     **/
    @Bean
    open fun localRepository(): BundleRepository{
        return BundleRepository(
                rsyncModuleUri = Rsync.URI(storageConfiguration.bundleRepositoryDirectory))
    }

    /**
     * Application wide bundle installer
     */
    @Bean
    open fun bundleInstaller(): BundleInstaller {
        return BundleInstaller(
                storageConfiguration.bundleInstallationDirectory)
    }
}