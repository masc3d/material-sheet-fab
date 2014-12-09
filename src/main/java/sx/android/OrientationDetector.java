package sx.android;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;
import sx.util.EventDelegate;
import sx.util.EventDispatcher;
import sx.util.EventListener;

/**
 * Orientation detector
 * Created by masc on 09.12.14.
 */
public class OrientationDetector {
    public enum OrientationType {
        Portrait,
        ReversePortrait,
        Landscape,
        ReverseLandscape
    }
    public interface Listener extends EventListener {
        void onOrientationDetectorUpdated(OrientationType orientation);
    }
    Context mContext;
    boolean mFirstOrientation = false;
    OrientationEventListener mOrientationEventListener;
    EventDispatcher<Listener> mEventDispatcher = EventDispatcher.createThreadSafe();

    int mThresholdDegrees = 15;

    public OrientationDetector(Context context) {
        mContext = context;

        // Orientation listener
        mOrientationEventListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_GAME) {

            @Override
            public void onOrientationChanged(int orientation) {
                OrientationType orientationType = null;

                if (orientation != ORIENTATION_UNKNOWN) {
                    if (orientation > 270 - mThresholdDegrees && orientation < 270 + mThresholdDegrees) {
                        orientationType = OrientationType.Landscape;
                    } else if (orientation >= 359 - mThresholdDegrees || orientation < mThresholdDegrees) {
                        orientationType = OrientationType.Portrait;
                    } else if (orientation > 90 - mThresholdDegrees && orientation < 90 + mThresholdDegrees) {
                        orientationType = OrientationType.ReverseLandscape;
                    } else if (orientation > 180 - mThresholdDegrees && orientation < 180 + mThresholdDegrees) {
                        orientationType = OrientationType.ReversePortrait;
                    }

                    if (orientationType != null) {
                        if (mFirstOrientation) {
                            mFirstOrientation = false;
                            final OrientationType finalOrientationType = orientationType;
                            mEventDispatcher.emit(new EventDispatcher.Runnable<Listener>() {
                                @Override
                                public void run(Listener listener) {
                                    listener.onOrientationDetectorUpdated(finalOrientationType);
                                }
                            });
                        }
                    } else {
                        mFirstOrientation = true;
                    }
                }
            }
        };
    }

    public EventDelegate<Listener> getEventDelegate() {
        return mEventDispatcher;
    }

    public void setEnabled(boolean enabled) {
        mFirstOrientation = false;
        if (enabled)
            mOrientationEventListener.enable();
        else
            mOrientationEventListener.disable();
    }
}
