package sx.android.ui

import android.app.Activity
import android.support.v4.app.FragmentActivity
import sx.util.Cast

import java.io.PrintWriter

/**
 * Created by masc on 20.08.14.
 */
object FragmentManagerUtil {
    /**
     * Dump fragment manager state to System.out
     * @param activity Activity
     */
    fun dump(activity: Activity) {
        val fragmentActivity = Cast.`as`(FragmentActivity::class.java, activity)

        if (fragmentActivity != null) {
            fragmentActivity.supportFragmentManager.dump("", null,
                    PrintWriter(System.out, true), null)
        } else {
            activity.fragmentManager.dump("", null,
                    PrintWriter(System.out, true), null)
        }
    }
}
