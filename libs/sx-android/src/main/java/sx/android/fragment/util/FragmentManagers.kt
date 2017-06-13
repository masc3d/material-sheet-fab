package sx.android.fragment.util

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.v4.app.FragmentActivity
import sx.util.Cast

import java.io.PrintWriter

fun FragmentManager.dump() {
    this.dump("", null, java.io.PrintWriter(System.out, true), null)
}

fun android.support.v4.app.FragmentManager.dump() {
    this.dump("", null, java.io.PrintWriter(System.out, true), null)
}

fun android.support.v4.app.FragmentManager.withTransaction(block: (ft: android.support.v4.app.FragmentTransaction) -> Unit): Int {
    val ft = this.beginTransaction()
    block(ft)
    return ft.commit()
}

fun FragmentManager.withTransaction(block: (ft: FragmentTransaction) -> Unit): Int {
    val ft = this.beginTransaction()
    block(ft)
    return ft.commit()
}
