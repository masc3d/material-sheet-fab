package org.deku.leoz.central

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Created by masc on 30.05.15.
 */
class App : org.deku.leoz.node.App() {
    private val log = LogFactory.getLog(App::class.java)

    override val name: String
        get() = "leoz-central"

    override fun initialize() {
        // No JMS logging for leoz-central
        super.initialize(PROFILE_CENTRAL)
    }

    companion object {
        fun instance(): App {
            return org.deku.leoz.node.App.instance() as App
        }

        const val PROFILE_CENTRAL = "central"
    }
}
