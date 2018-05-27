package org.deku.leoz.node.config

import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import sx.rsync.Rsync
import javax.inject.Inject

/**
 * Created by masc on 09/03/16.
 */
@Configuration
@Lazy(false)
class BundleConfiguration {
    @Inject
    private lateinit var storage: Storage

    /**
     * Local bundle repository
     **/
    @get:Bean
    val localRepository: BundleRepository
        get() = BundleRepository(
                rsyncModuleUri = Rsync.URI(storage.bundleRepositoryDirectory))

    /**
     * Application wide bundle installer
     */
    @get:Bean
    val bundleInstaller: BundleInstaller
        get() = BundleInstaller(
                storage.bundleInstallationDirectory)
}