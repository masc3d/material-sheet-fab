package org.deku.leoz.mobile.ui

import android.support.annotation.MenuRes
import android.support.v7.view.menu.MenuBuilder
import android.view.Menu

/**
 * Helper for inflating menu on the fly
 * Created by masc on 15.06.17.
 */
fun Activity.inflateMenu(@MenuRes id: Int): Menu {
    val mb = MenuBuilder(this.applicationContext)
    this.menuInflater.inflate(id, mb)
    return mb
}