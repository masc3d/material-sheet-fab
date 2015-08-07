package org.deku.leo2;

/**
 * Leo2 global resources
 *
 * Created by masc on 22.09.14.
 */
public class Global {
    private static Global mInstance;

    public static Global instance() {
        if (mInstance == null) {
            synchronized(Global.class) {
                mInstance = new Global();
            }
        }
        return mInstance;
    }

    private Global() {
    }
}
