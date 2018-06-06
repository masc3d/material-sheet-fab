package sx.android.view

import android.content.Context
import android.hardware.SensorManager
import android.view.OrientationEventListener
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener

/**
 * Orientation detector
 * Created by masc on 09.12.14.
 */
class OrientationDetector(private var context: Context) {
    /**
     * Orientation type
     */
    enum class OrientationType {
        Portrait,
        ReversePortrait,
        Landscape,
        ReverseLandscape
    }

    interface Listener : EventListener {
        fun onOrientationDetectorUpdated(orientation: OrientationType)
    }

    private var isFirstOrientation = false
    private var orientationEventListener: OrientationEventListener
    private var thresholdDegrees = 15

    private var eventDispatcher = EventDispatcher.createThreadSafe<Listener>()
    val eventDelegate: EventDelegate<Listener>
        get() = eventDispatcher


    init {
        // Orientation listener
        orientationEventListener = object : OrientationEventListener(context, SensorManager.SENSOR_DELAY_GAME) {

            override fun onOrientationChanged(orientation: Int) {
                var orientationType: OrientationType? = null

                if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
                    if (orientation > 270 - thresholdDegrees && orientation < 270 + thresholdDegrees) {
                        orientationType = OrientationType.Landscape
                    } else if (orientation >= 359 - thresholdDegrees || orientation < thresholdDegrees) {
                        orientationType = OrientationType.Portrait
                    } else if (orientation > 90 - thresholdDegrees && orientation < 90 + thresholdDegrees) {
                        orientationType = OrientationType.ReverseLandscape
                    } else if (orientation > 180 - thresholdDegrees && orientation < 180 + thresholdDegrees) {
                        orientationType = OrientationType.ReversePortrait
                    }

                    if (orientationType != null) {
                        if (isFirstOrientation) {
                            isFirstOrientation = false
                            val finalOrientationType = orientationType
                            eventDispatcher.emit { listener -> listener.onOrientationDetectorUpdated(finalOrientationType) }
                        }
                    } else {
                        isFirstOrientation = true
                    }
                }
            }
        }
    }

    var enabled: Boolean = false
        set(value: Boolean) {
            isFirstOrientation = false
            if (value)
                orientationEventListener.enable()
            else
                orientationEventListener.disable()
            field = value
        }
}
