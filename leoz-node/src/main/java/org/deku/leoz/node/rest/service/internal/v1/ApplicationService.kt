package org.deku.leoz.node.rest.service.internal.v1

import sx.packager.BundleInstaller
import org.deku.leoz.service.update.BundleUpdateService
import org.deku.leoz.bundle.boot
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.deku.leoz.rest.entity.internal.v1.ApplicationVersion
import org.slf4j.LoggerFactory
import sx.jms.Channel
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 09.10.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/application")
class ApplicationService : org.deku.leoz.rest.service.internal.v1.ApplicationService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var storage: Storage
    @Inject
    private lateinit var bundleUpdateService: BundleUpdateService

    override fun restart() {
        val bundleInstaller = BundleInstaller(
                storage.bundleInstallationDirectory)

        bundleInstaller.boot(this.application.name)
    }

    override fun getVersion(): ApplicationVersion {
        return ApplicationVersion(
                this.application.name,
                this.application.version)
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