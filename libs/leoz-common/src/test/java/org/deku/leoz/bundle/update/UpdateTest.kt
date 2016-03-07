package org.deku.leoz.bundle.update

import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.BundleTestConfiguration
import org.deku.leoz.config.StorageTestConfiguration
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.junit.Test
import sx.jms.Channel

/**
 * Created by masc on 12.10.15.
 */
class UpdateTest {
    val updater = BundleUpdater(
            identity = Identity.create(Bundles.LEOZ_BOOT.value, SystemInformation()),
            installer = BundleInstaller(
                    StorageTestConfiguration.bundlesTestDirectory),
            remoteRepository = BundleTestConfiguration.remoteRepository,
            presets = listOf(),
            updateInfoRequestChannel = Channel(ActiveMQConfiguration.instance.centralQueue))

    @Test
    fun testRun() {
    }
}