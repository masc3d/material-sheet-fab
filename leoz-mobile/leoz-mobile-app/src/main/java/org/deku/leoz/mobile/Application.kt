package org.deku.leoz.mobile

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.config.*
import org.deku.leoz.mobile.settings.DebugSettings
import org.deku.leoz.mobile.ui.BaseActivity
import org.slf4j.LoggerFactory


/**
 * Application
 * Created by masc on 10/12/2016.
 */
open class Application : MultiDexApplication() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    internal val bundle = Bundle()

    var isInitialized: Boolean = false

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

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleContextWrapper.wrap(context = base!!, language = null))
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
        applicationInfo.loadLabel(this.packageManager).toString()
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

val BaseActivity.app: Application get() = this.application as Application