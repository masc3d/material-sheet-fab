package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.boot
import org.deku.leoz.node.App
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 09.10.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/application")
@Produces(MediaType.APPLICATION_JSON)
class ApplicationService : org.deku.leoz.rest.services.internal.v1.ApplicationService {

    @Inject
    lateinit var bundleUpdater: BundleUpdater

    override fun restart() {
        val bundleInstaller = BundleInstaller(
                StorageConfiguration.instance.bundlesDirectory)

        bundleInstaller.boot(App.instance.name)
    }

    override fun getVersion(): ApplicationVersion {
        return ApplicationVersion(
                App.instance.jarManifest.implementationName,
                App.instance.jarManifest.implementationVersion)
    }

    override fun updateBundles() {
        this.bundleUpdater.startUpdate()
    }
}