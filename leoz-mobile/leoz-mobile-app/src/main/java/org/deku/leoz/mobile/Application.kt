package org.deku.leoz.mobile

import android.content.Context
import android.content.pm.PackageManager
import android.support.multidex.MultiDexApplication
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.mobile.config.DatabaseConfiguration
import org.deku.leoz.mobile.config.LogConfiguration
import org.deku.leoz.mobile.config.StorageConfiguration
import org.slf4j.LoggerFactory

/**
 * Application
 * Created by n3 on 10/12/2016.
 */
open class Application : MultiDexApplication() {
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate() {
        super.onCreate()

        Kodein.global.addImport(Kodein.Module {
            bind<Context>() with singleton { this@Application.applicationContext }
            bind<Application>() with singleton { this@Application }
        })
        Kodein.global.addImport(androidModule)
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
        Kodein.global.addImport(DatabaseConfiguration.module)

        log.info("${this.name} v${this.version} application start")
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    /**
     * Application version
     * @return
     */
    val version: String by lazy {
        try {
            this.packageManager.getPackageInfo(this.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            log.debug(e.message)
            ""
        }
    }

    /**
     * Application name
     */
    val name: String by lazy {
        applicationInfo.loadLabel(this.packageManager).toString();
    }
}