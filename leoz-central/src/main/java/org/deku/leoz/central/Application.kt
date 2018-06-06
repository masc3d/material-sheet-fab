package org.deku.leoz.central

import org.deku.leoz.bundle.BundleType
import org.slf4j.LoggerFactory

/**
 * Application instance.
 * Derives from leoz node, overriding app specifics like spring profile and application name
 * Created by masc on 30.05.15.
 */
class Application : org.deku.leoz.node.Application() {
    private val log = LoggerFactory.getLogger(Application::class.java)

    override val type: Class<out Any>
        get() = Application::class.java

    override val bundleType: BundleType
        get() = BundleType.LEOZ_CENTRAL

    override fun initialize() {
        // No JMS logging for leoz-central
        super.initialize(PROFILE_CENTRAL)
    }

    companion object {
        const val PROFILE_CENTRAL = "leoz-central"
    }
}
