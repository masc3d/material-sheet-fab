package org.deku.leoz.mobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import com.github.salomonbrys.kodein.lazy
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.config.*
import org.slf4j.LoggerFactory


/**
 * Application
 * Created by masc on 10/12/2016.
 */
open class Application : MultiDexApplication() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    internal val bundle = Bundle()

    override fun onCreate() {
        super.onCreate()

        // region Injection setup
        // Base modules
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
        Kodein.global.addImport(SettingsConfiguration.module)

        //Android specific modules
        Kodein.global.addImport(androidModule)
        Kodein.global.addImport(Kodein.Module {
            bind<Context>() with singleton { this@Application.applicationContext }
            bind<Application>() with singleton { this@Application }
            bind<android.app.Application>() with singleton { this@Application }
        })

        // Higher level modules
        Kodein.global.addImport(ApplicationConfiguration.module)
        Kodein.global.addImport(ExecutorConfiguration.module)
        Kodein.global.addImport(DatabaseConfiguration.module)
        Kodein.global.addImport(RepositoryConfiguration.module)
        Kodein.global.addImport(ModelConfiguration.module)
        Kodein.global.addImport(RestClientConfiguration.module)
        Kodein.global.addImport(ServiceConfiguration.module)
        Kodein.global.addImport(DeviceConfiguration.module)
        Kodein.global.addImport(AidcConfiguration.module)
        Kodein.global.addImport(SharedPreferenceConfiguration.module)
        Kodein.global.addImport(ToneConfiguration.module)
        Kodein.global.addImport(MqttConfiguration.module)
        //endregion

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

        // Enable app compat vector support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (!BuildConfig.DEBUG) {
            // FlexibleAdapter logging is currently not compatible with obfuscated builds
            // Internally tries to extract a stack trace element which does not
            // work with obfuscated class names (presumably)
            // TODO: should be investigated/reported/fixed upstream
            eu.davidea.flexibleadapter.utils.Log.setLevel(
                    eu.davidea.flexibleadapter.utils.Log.Level.SUPPRESS)
        }

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

    /**
     * Terminate/kill application immediately
     */
    fun terminate() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    //region Component callbacks
    override fun onConfigurationChanged(newConfig: Configuration) {
        log.trace("CONFIGURATION CHANGE")

        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        log.trace("MEMORY LOW")

        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        log.trace("MEMORY TRIMMED [${level}]")

        super.onTrimMemory(level)
    }
    //endregion
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
