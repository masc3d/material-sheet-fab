package org.deku.leoz.mobile

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.mobile.config.*
import org.slf4j.LoggerFactory

/**
 * Application
 * Created by masc on 10/12/2016.
 */
open class Application : MultiDexApplication() {
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    val bundle = Bundle()

    override fun onCreate() {
        super.onCreate()
        Log.v("", "ONCREATE")

        // Add core injections (usually light-weight, shared with instrumentation tests)
        Kodein.global.addImport(androidModule)
        Kodein.global.addImport(Kodein.Module {
            bind<Context>() with singleton { this@Application.applicationContext }
            bind<Application>() with singleton { this@Application }
        })
        Kodein.global.addImport(ExecutorConfiguration.module)
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
        Kodein.global.addImport(DatabaseConfiguration.module)
        Kodein.global.addImport(UpdateConfiguration.module)
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

val Activity.app: Application get() = this.application as Application
