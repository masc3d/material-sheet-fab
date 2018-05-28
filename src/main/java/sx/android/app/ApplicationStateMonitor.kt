package sx.android.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory

/**
 * Application state monitor.
 * For application state to be reported reliably, instance has to be created during application initialization (before any activity actually starts)
 * Created by masc on 20.07.17.
 */
class ApplicationStateMonitor(
        application: Application
)  {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    private val handler = Handler()

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


    init {
        log.info("Registering lifecycle callbacks")
        application.registerActivityLifecycleCallbacks(this.handler)
    }

    inner class Handler : Application.ActivityLifecycleCallbacks {
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

            if (activityCount == 0) {
                log.trace("APPLICATION FOREGROUND")
                stateChangedEventSubject.onNext(StateType.Foreground)
            }

            activityCount++
        }

        override fun onActivityStopped(activity: Activity) {
            log.trace("ACTIVITY STOPPED [${activity}]")
            activityCount--

            if (activityCount < 0)
                activityCount = 0

            if (activityCount == 0) {
                log.trace("APPLICATION BACKGROUND")
                stateChangedEventSubject.onNext(StateType.Background)
            }
        }
    }
}