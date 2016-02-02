package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.boot
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.node.data.sync.v1.EntityStateMessage
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import sx.jms.Channel
import sx.jms.converters.DefaultConverter
import sx.rs.ApiKey
import java.time.Duration
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
                StorageConfiguration.instance.bundleInstallationDirectory)

        bundleInstaller.boot(App.instance.name)
    }

    override fun getVersion(): ApplicationVersion {
        return ApplicationVersion(
                App.instance.jarManifest.implementationName,
                App.instance.jarManifest.implementationVersion)
    }

    override fun bundleUpdate() {
        this.bundleUpdater.startUpdate()
    }

    override fun notifyBundleUpdate(bundleName: String) {
        // TODO. centralize channel(s) into common configuration
        val mc = Channel(
                ActiveMQConfiguration.instance.broker.connectionFactory,
                ActiveMQConfiguration.instance.nodeEntitySyncTopic,
                DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP),
                Duration.ofSeconds(10),
                false,
                Channel.DeliveryMode.NonPersistent,
                Duration.ofMinutes(5))

        mc.send(UpdateInfo(bundleName))
    }
}