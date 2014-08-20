package org.sx.android;

import android.app.Activity;

import java.io.PrintWriter;

/**
 * Created by masc on 20.08.14.
 */
public class FragmentManagerUtil {

    /** Dump fragment manager state to System.out */
    public static void dump(Activity a) {
        a.getFragmentManager().dump("", null,
                new PrintWriter(System.out, true), null);
    }
}
