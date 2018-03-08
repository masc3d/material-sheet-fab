package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.boot.config.*
import org.slf4j.LoggerFactory
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.internal.DiscoveryService
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
                log.trace("Setting up injection")
                Kodein.global.addImport(ApplicationConfiguration.module)
                Kodein.global.addImport(StorageConfiguration.module)
                Kodein.global.addImport(LogConfiguration.module)
                Kodein.global.addImport(RsyncConfiguration.module)
                Kodein.global.addImport(DiscoveryConfiguration.module)
                Kodein.global.addImport(RestClientConfiguration.module)
                Kodein.global.addImport(BundleConfiguration.module)
                Kodein.global.addImport(SshConfiguration.module)
                log.trace("Done setting up injection")

                val settings = Kodein.global.instance<Settings>()

                // Parse leoz-boot command line
                val jc = JCommander(settings)
                jc.setAcceptUnknownOptions(true)
                jc.parse(*args)

                if (jc.unknownOptions.size > 0) {
                    jc.usage()
                    System.exit(0)
                    return
                }

                log.info("${settings}")

                Kodein.global.instance<RsyncConfiguration>()
                Kodein.global.instance<DiscoveryService>().also {
                    it.start()
                }

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
