package org.deku.leoz.central

import org.slf4j.LoggerFactory
import sx.LazyInstance

/**
 * Application instance.
 * Derives from leoz node, overriding app specifics like spring profile and application name
 * Created by masc on 30.05.15.
 */
class App private constructor() : org.deku.leoz.node.App() {
    private val log = LoggerFactory.getLogger(App::class.java)

    override val name: String
        get() = "leoz-central"

    override val type: Class<out Any>
        get() = App::class.java

    override fun initialize() {
        // No JMS logging for leoz-central
        super.initialize(PROFILE_CENTRAL)
    }

    companion object {
        @JvmStatic val injectableInstance = LazyInstance({ App() })
        @JvmStatic val instance by lazy({ injectableInstance.get() })

        const val PROFILE_CENTRAL = "central"
    }
}
