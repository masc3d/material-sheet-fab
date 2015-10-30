package org.deku.leoz.update

import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepositoryFactory
import org.deku.leoz.config.StorageTestConfiguration
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.junit.Test

/**
 * Created by masc on 12.10.15.
 */
class UpdateTest {
    val updater = Updater(
            identity = Identity.create(SystemInformation()),
            bundleInstaller = BundleInstaller(
                    StorageTestConfiguration.bundlesDirectory,
                    BundleRepositoryFactory.stagingRepository()),
            jmsConnectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
            jmsUpdateRequestQueue = ActiveMQConfiguration.instance.centralQueue)

    @Test
    fun testRun() {
    }
}