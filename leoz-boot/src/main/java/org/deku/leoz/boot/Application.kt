package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.boot.config.*
import org.slf4j.LoggerFactory
import io.reactivex.rxkotlin.subscribeBy
import java.awt.GraphicsEnvironment

/**
 * Main application (javafx) class
 * Created by masc on 29-Jul-15.
 */
class Application {
    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)

        /**
         * Main application entry point
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                // Leoz bundle process commandline interface support
                val setup = Setup()

                val command = setup.parse(args)
                if (command != null) {
                    command.run()
                    System.exit(0)
                    return
                }

                // Uncaught threaded exception handler
                Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                    override fun uncaughtException(t: Thread, e: Throwable) {
                        log.error(e.message, e)
                    }
                })

                // Injection setup
                log.info("Setting up injection")
                Kodein.global.addImport(ApplicationConfiguration.module)
                Kodein.global.addImport(StorageConfiguration.module)
                Kodein.global.addImport(LogConfiguration.module)
                Kodein.global.addImport(RsyncConfiguration.module)
                Kodein.global.addImport(DiscoveryConfiguration.module)
                Kodein.global.addImport(RestClientFactory.module)
                Kodein.global.addImport(BundleConfiguration.module)
                Kodein.global.addImport(SshConfiguration.module)
                log.info("Done setting up injection")

                val settings = Kodein.global.instance<Settings>()
                log.info("${settings}")

                // Parse leoz-boot command line
                val jc = JCommander(settings)
                jc.parse(*args)

                if (settings.hideUi || GraphicsEnvironment.isHeadless()) {
                    Boot().boot(settings).subscribeBy(
                            onComplete = {
                                System.exit(0)
                            },
                            onError = {
                                log.error(it.message, it)
                                System.exit(-1)
                            })
                } else {
                    javafx.application.Application.launch(org.deku.leoz.boot.fx.Application::class.java)
                }

            } catch (e: Exception) {
                log.error(e.message, e)
                System.exit(-1)
            }
        }
    }
}
