package org.deku.leoz.node.rest.services.internal.v1

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.boot
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.config.StorageConfiguration
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
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    lateinit var bundleUpdater: BundleUpdater

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
        this.bundleUpdater.startUpdate()
    }

    override fun notifyBundleUpdate(bundleName: String) {
        val message = UpdateInfo(bundleName)
        // TODO. centralize channel(s) into common configuration
        Channel(
                connectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
                destination = ActiveMQConfiguration.instance.nodeNotificationTopic,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP),
                jmsTtl = Duration.ofMinutes(5)).use { c ->
            c.send(UpdateInfo(bundleName))
        }

        log.info("Sent [${message}]")
    }
}