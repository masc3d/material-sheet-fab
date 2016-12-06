package org.deku.leoz.node.test

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.ApplicationConfiguration
import org.slf4j.LoggerFactory

/**
 * Created by masc on 18.06.15.
 */
class AppTest {
    companion object {
        init {
            val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
            lRoot.level = Level.INFO

            Kodein.global.addImport(ApplicationConfiguration.module)

            Kodein.global.instance<Application>().initialize()
        }
    }
}
