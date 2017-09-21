package org.deku.leoz.node.service.internal

import org.deku.leoz.node.*
import org.deku.leoz.bundle.boot
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.service.entity.internal.update.BundleUpdateService
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.deku.leoz.service.internal.ApplicationService.Version
import sx.mq.jms.channel
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 09.10.15.
 */
@Named
@Path("internal/v1/application")
class ApplicationService : org.deku.leoz.service.internal.ApplicationService {

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var storage: Storage
    @Inject
    private lateinit var bundleUpdateService: BundleUpdateService

    override fun restart() {
        val bundleInstaller = sx.packager.BundleInstaller(
                storage.bundleInstallationDirectory)

        bundleInstaller.boot(this.application.name)
    }

    override fun getVersion(): Version {
        return Version(
                this.application.name,
                this.application.version)
    }

    override fun bundleUpdate() {
        this.bundleUpdateService.trigger()
    }

    override fun notifyBundleUpdate(bundleName: String) {
        val message = UpdateInfo(bundleName)

        JmsEndpoints.node.topic.channel().use {
            it.send(UpdateInfo(bundleName))
        }

        log.info("Sent [${message}]")
    }
}