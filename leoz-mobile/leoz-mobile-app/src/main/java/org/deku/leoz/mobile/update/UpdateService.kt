package org.deku.leoz.mobile.update

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.slf4j.LoggerFactory
import sx.concurrent.Service
import sx.time.seconds
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * Application/APK update service
 * Created by masc on 10/02/2017.
 */
class UpdateService(executorService: ScheduledExecutorService) : Service(
        executorService = executorService,
        initialDelay = 5.seconds,
        period = 20.seconds
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val bundleService: BundleService by Kodein.global.lazy.instance()

    override fun run() {
        log.info("Update cycle")

        try {
            val updateInfo = this.bundleService.info("leoz-mobile", "snapshot")
            log.info("${updateInfo}")
        } catch(e: Exception) {
            log.error(e.message)
        }
   }
}