package org.deku.leoz.mobile

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import org.deku.leoz.mobile.config.*
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.android.Device
import sx.android.aidc.BarcodeReader
import sx.android.honeywell.aidc.HoneywellBarcodeReader

/**
 * Application
 * Created by masc on 10/12/2016.
 */
open class Application : MultiDexApplication(), android.app.Application.ActivityLifecycleCallbacks {
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    internal val bundle = Bundle()

    override fun onCreate() {
        super.onCreate()

        // Base modules
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
        Kodein.global.addImport(SettingsConfiguration.module)

        // Android specific modules
        Kodein.global.addImport(androidModule)
        Kodein.global.addImport(Kodein.Module {
            bind<Context>() with singleton { this@Application.applicationContext }
            bind<Application>() with singleton { this@Application }
            bind<android.app.Application>() with singleton { this@Application }
        })

        // Higher level modules
        Kodein.global.addImport(ExecutorConfiguration.module)
        Kodein.global.addImport(DatabaseConfiguration.module)
        Kodein.global.addImport(FeignRestClientConfiguration.module)
        Kodein.global.addImport(UpdateConfiguration.module)
        Kodein.global.addImport(DeviceConfiguration.module)
        Kodein.global.addImport(AidcConfiguration.module)

        this.registerActivityLifecycleCallbacks(this)
    }

    override fun onTerminate() {
        this.unregisterActivityLifecycleCallbacks(this)
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

    /**
     * Terminate/kill application immediately
     */
    fun terminate() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    override fun onConfigurationChanged(p0: Configuration?) {
        log.info("CONFIGCHANGE")
    }

    override fun onLowMemory() {
        log.info("LOWMEM")
    }

    override fun onTrimMemory(p0: Int) {
        log.info("TRIMMEM")
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        log.info("ACT_CREATED [${p0}]")
    }

    override fun onActivityDestroyed(p0: Activity?) {
        log.info("ACT_DESTROYED [${p0}]")
    }

    override fun onActivityPaused(p0: Activity?) {
        log.info("ACT_PAUSED [${p0}]")
    }

    override fun onActivityResumed(p0: Activity?) {
        log.info("ACT_RESUMED [${p0}]")
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
        log.info("ACT_SAVEINSTANCESTATE [${p0}]")
    }

    override fun onActivityStarted(p0: Activity?) {
        log.info("ACT_STARTED [${p0}]")
    }

    override fun onActivityStopped(p0: Activity?) {
        log.info("ACT_STOPPED [${p0}]")
    }
}

val Activity.app: Application get() = this.application as Application

/**
 * Freezes instance state within application bundle
 */
fun Application.freezeInstanceState(activity: Activity) {
    // Save state
    val bundle = Bundle()
    activity.freezeInstanceState(bundle)
    this.bundle.putBundle(activity.localClassName, bundle)
}

/**
 * Unfreeze instance state from application bundle
 */
fun Application.unfreezeInstanceState(activity: Activity) {
    // Restore state
    val bundle = this.bundle.getBundle(activity.localClassName)
    if (bundle != null) {
        activity.unfreezeInstanceState(bundle)
    }
}
