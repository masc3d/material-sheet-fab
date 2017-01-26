package org.deku.leoz.node.config

import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import org.deku.leoz.config.BundleConfiguration
import org.deku.leoz.node.Storage
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
    private lateinit var storage: Storage

    /**
     * Local bundle repository
     **/
    @Bean
    open fun localRepository(): BundleRepository {
        return BundleRepository(
                rsyncModuleUri = Rsync.URI(storage.bundleRepositoryDirectory))
    }

    /**
     * Application wide bundle installer
     */
    @Bean
    open fun bundleInstaller(): BundleInstaller {
        return BundleInstaller(
                storage.bundleInstallationDirectory)
    }
}