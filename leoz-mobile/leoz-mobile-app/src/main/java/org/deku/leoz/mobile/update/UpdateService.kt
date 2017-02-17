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
import android.support.v4.content.ContextCompat.startActivity
import android.R.attr.path
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File


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
    private val context: Context by Kodein.global.lazy.instance()

    override fun run() {
        log.info("Update cycle")

        try {
            val updateInfo = this.bundleService.info("leoz-mobile", "snapshot")
            log.info("${updateInfo}")

            val apkFile = File("")
            log.info("Installing apk")

            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            this.context.startActivity(i)
        } catch(e: Exception) {
            log.error(e.message)
        }
   }
}