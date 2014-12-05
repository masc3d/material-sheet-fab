package sx.android.ui;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import sx.util.Cast;

import java.io.PrintWriter;

/**
 * Created by masc on 20.08.14.
 */
public class FragmentManagerUtil {

    /**
     * Dump fragment manager state to System.out
     *
     * @param activity Activity
     */
    public static void dump(Activity activity) {
        FragmentActivity fragmentActivity = Cast.as(FragmentActivity.class, activity);

        if (fragmentActivity != null) {
            fragmentActivity.getSupportFragmentManager().dump("", null,
                    new PrintWriter(System.out, true), null);
        } else {
            activity.getFragmentManager().dump("", null,
                    new PrintWriter(System.out, true), null);
        }
    }
}
