package org.deku.leoz.mobile

import android.app.Activity
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
open class Application : MultiDexApplication(), android.app.Application.ActivityLifecycleCallbacks {
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

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

        this.registerActivityLifecycleCallbacks(this)

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

    private var activityCount: Int = 0

    /**
     * Application state
     */
    enum class StateType {
        Foreground,
        Background
    }

    private val stateChangedEventSubject = PublishSubject.create<StateType>()
    /** Application state change event */
    val stateChangedEvent = this.stateChangedEventSubject.hide()

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

    //region Lifecycle callbacks
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        log.trace("ACTIVITY CREATED [${activity}]")
    }

    override fun onActivityDestroyed(activity: Activity) {
        log.trace("ACTIVITY DESTROYED [${activity}]")
    }

    override fun onActivityPaused(activity: Activity) {
        log.trace("ACTIVITY PAUSED [${activity}]")
    }

    override fun onActivityResumed(activity: Activity) {
        log.trace("ACTIVITY RESUMED [${activity}]")
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
        log.trace("ACTIVITY SAVEINSTANCESTATE [${activity}]")
    }

    override fun onActivityStarted(activity: Activity) {
        log.trace("ACTIVITY STARTED [${activity}]")
        if (this.activityCount == 0) {
            log.trace("APPLICATION FOREGROUND")
            this.stateChangedEventSubject.onNext(StateType.Foreground)
        }
        this.activityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        log.trace("ACTIVITY STOPPED [${activity}]")
        this.activityCount--

        if (this.activityCount == 0) {
            log.trace("APPLICATION BACKGROUND")
            this.stateChangedEventSubject.onNext(StateType.Background)
        }
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
