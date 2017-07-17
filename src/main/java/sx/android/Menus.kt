package sx.android

import android.app.Activity
import android.content.Context
import android.support.annotation.MenuRes
import android.support.v4.app.Fragment
import android.view.Menu

// Extensions for inflating menus on the fly

fun Activity.inflateMenu(@MenuRes id: Int): Menu {
    val mb = android.support.v7.view.menu.MenuBuilder(this.baseContext)
    this.menuInflater.inflate(id, mb)
    return mb
}

fun Fragment.inflateMenu(@MenuRes id: Int): Menu {
    val mb = android.support.v7.view.menu.MenuBuilder(this.context)
    this.activity.menuInflater.inflate(id, mb)
    return mb
}