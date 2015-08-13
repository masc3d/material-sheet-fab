package org.deku.leoz;

/**
 * Created by masc on 21.10.14.
 */
public class Settings {
    private static Settings mInstance;

    private boolean mAnimationsEnabled;

    public boolean isAnimationsEnabled() {
        return mAnimationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        mAnimationsEnabled = animationsEnabled;
    }

    private Settings() {
        mAnimationsEnabled = false;
    }

    public static Settings instance() {
        if (mInstance == null) {
            synchronized(Global.class) {
                mInstance = new Settings();
            }
        }
        return mInstance;
    }

}
