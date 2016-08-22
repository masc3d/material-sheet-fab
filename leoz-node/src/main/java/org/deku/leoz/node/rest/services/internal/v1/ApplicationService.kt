package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.update.BundleUpdateService
import org.deku.leoz.bundle.boot
import org.deku.leoz.bundle.update.UpdateInfo
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import org.slf4j.LoggerFactory
import sx.jms.Channel
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 09.10.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/application")
class ApplicationService : org.deku.leoz.rest.services.internal.v1.ApplicationService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var bundleUpdateService: BundleUpdateService

    override fun restart() {
        val bundleInstaller = BundleInstaller(
                StorageConfiguration.instance.bundleInstallationDirectory)

        bundleInstaller.boot(App.instance.name)
    }

    override fun getVersion(): ApplicationVersion {
        return ApplicationVersion(
                App.instance.name,
                App.instance.version)
    }

    override fun bundleUpdate() {
        this.bundleUpdateService.trigger()
    }

    override fun notifyBundleUpdate(bundleName: String) {
        val message = UpdateInfo(bundleName)

        Channel(ActiveMQConfiguration.instance.nodeNotificationTopic).use {
            it.send(UpdateInfo(bundleName))
        }

        log.info("Sent [${message}]")
    }
}