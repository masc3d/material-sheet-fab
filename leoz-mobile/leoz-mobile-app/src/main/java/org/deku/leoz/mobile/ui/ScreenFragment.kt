package org.deku.leoz.mobile.ui

import org.deku.leoz.mobile.ui.view.ActionItem
import sx.rx.ObservableRxProperty

/**
 * Created by masc on 14.06.17.
 */
open class ScreenFragment : Fragment() {
    val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(listOf())
    var actionItems by actionItemsProperty

    interface Listener {
        fun onScreenFragmentResume(fragment: ScreenFragment) {}
        fun onScreenFragmentPause(fragment: ScreenFragment) {}
    }

    private val listener get() = this.activity

    override fun onResume() {
        super.onResume()
        this.listener.onScreenFragmentResume(this)
    }

    override fun onPause() {
        super.onPause()
        this.listener.onScreenFragmentPause(this)
    }
}