package sx.android.fragment.util

import android.app.Activity
import android.app.FragmentManager
import android.support.v4.app.FragmentActivity
import sx.util.Cast

import java.io.PrintWriter

fun FragmentManager.dump() {
    this.dump("", null, java.io.PrintWriter(System.out, true), null)
}

fun android.support.v4.app.FragmentManager.dump() {
    this.dump("", null, java.io.PrintWriter(System.out, true), null)
}
