package org.deku.leoz.mobile

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.config.*
import org.slf4j.LoggerFactory
import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Application
 * Created by masc on 10/12/2016.
 */
open class Application : MultiDexApplication(), android.app.Application.ActivityLifecycleCallbacks {
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    internal val bundle = Bundle()

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

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
        Kodein.global.addImport(ModelConfiguration.module)
        Kodein.global.addImport(RestClientConfiguration.module)
        Kodein.global.addImport(ServiceConfiguration.module)
        Kodein.global.addImport(DeviceConfiguration.module)
        Kodein.global.addImport(AidcConfiguration.module)
        Kodein.global.addImport(SharedPreferenceConfiguration.module)
        Kodein.global.addImport(ToneConfiguration.module)
        Kodein.global.addImport(MqttConfiguration.module)

        this.registerActivityLifecycleCallbacks(this)

        //region Global exception handler
        run {
            val androidHandler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
                log.error("Uncaught exception [${paramThread}] ${paramThrowable.message}", paramThrowable)

                // Stop log appender to make sure all logs are flushed before termination
                Kodein.global.instance<LogMqAppender>().stop()

                if (androidHandler != null) {
                    // Delegate back to android's exception handler
                    androidHandler.uncaughtException(
                            paramThread,
                            paramThrowable
                    )
                } else {
                    // Service apps may not have an exception handler, just terminating
                    System.exit(1)
                }
            }
        }
        //endregion

        if (!BuildConfig.DEBUG) {
            // FlexibleAdapter logging is currently not compatible with obfuscated builds
            // Internally tries to extract a stack trace element which does not
            // work with obfuscated class names (presumably)
            // TODO: should be investigated/fixed upstream
            eu.davidea.flexibleadapter.utils.Log.setLevel(
                    eu.davidea.flexibleadapter.utils.Log.Level.SUPPRESS)
        }
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
        log.trace("CONFIGCHANGE")
    }

    override fun onLowMemory() {
        log.trace("LOWMEM")
    }

    override fun onTrimMemory(p0: Int) {
        log.trace("TRIMMEM")
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        log.trace("ACT_CREATED [${p0}]")
    }

    override fun onActivityDestroyed(p0: Activity?) {
        log.trace("ACT_DESTROYED [${p0}]")
    }

    override fun onActivityPaused(p0: Activity?) {
        log.trace("ACT_PAUSED [${p0}]")
    }

    override fun onActivityResumed(p0: Activity?) {
        log.trace("ACT_RESUMED [${p0}]")
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
        log.trace("ACT_SAVEINSTANCESTATE [${p0}]")
    }

    override fun onActivityStarted(p0: Activity?) {
        log.trace("ACT_STARTED [${p0}]")
    }

    override fun onActivityStopped(p0: Activity?) {
        log.trace("ACT_STOPPED [${p0}]")
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
