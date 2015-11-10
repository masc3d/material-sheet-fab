package org.deku.leoz.central

import org.apache.commons.logging.LogFactory

/**
 * Application instance.
 * Derives from leoz node, overriding app specifics like spring profile and application name
 * Created by masc on 30.05.15.
 */
class App private constructor() : org.deku.leoz.node.App() {
    private val log = LogFactory.getLog(App::class.java)

    override val name: String
        get() = "leoz-central"

    override fun initialize() {
        // No JMS logging for leoz-central
        super.initialize(PROFILE_CENTRAL)
    }

    companion object {
        val instance by lazy({
            App()
        })

        const val PROFILE_CENTRAL = "central"
    }
}
