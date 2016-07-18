package sx.android.app

import android.app.Activity
import android.support.v4.app.FragmentActivity
import sx.util.Cast

import java.io.PrintWriter

fun android.app.FragmentManager.dump() {
    this.dump("", null, PrintWriter(System.out, true), null)
}

fun android.support.v4.app.FragmentManager.dump() {
    this.dump("", null, PrintWriter(System.out, true), null)
}
