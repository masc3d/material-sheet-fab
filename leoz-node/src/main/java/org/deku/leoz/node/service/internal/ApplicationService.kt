package org.deku.leoz.node.service.internal

import org.deku.leoz.bundle.boot
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.deku.leoz.node.service.internal.sync.EntityConsumer
import org.deku.leoz.service.internal.ApplicationService.Version
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.deku.leoz.service.internal.update.BundleUpdateService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.mq.jms.channel
import java.util.*
import javax.inject.Inject
import javax.ws.rs.Path

@Component
@Path("internal/v1/application")
@Profile(Application.PROFILE_NODE)
class ApplicationService : org.deku.leoz.service.internal.ApplicationService {

    protected val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var application: Application

    @Inject
    private lateinit var storage: Storage

    @Inject
    private lateinit var bundleUpdateService: BundleUpdateService

    @Inject
    private lateinit var entityConsumer: Optional<EntityConsumer>

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

        JmsEndpoints.node.broadcast.channel().use {
            it.send(UpdateInfo(bundleName))
        }

        log.info("Sent [${message}]")
    }

    override fun syncWithRemoteNode(clean: Boolean) {
        this.entityConsumer.ifPresent {
            it.request(clean = clean)
        }
    }

    override fun syncWithCentralDatabase(clean: Boolean) {
        throw UnsupportedOperationException()
    }
}