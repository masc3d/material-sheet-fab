package org.deku.leoz.mobile.ui

import org.deku.leoz.mobile.ui.view.ActionItem
import sx.rx.ObservableRxProperty

/**
 * Created by masc on 14.06.17.
 */
open class ScreenFragment : Fragment() {
    val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(listOf())
    var actionItems by actionItemsProperty

    /**
     * Enables collapsing toolbar's scroll/snap mode.
     * This should be disabled for screens with static/ottom aligned content, as it may be partially
     * off screen when this mode is enabled.
     */
    var scrollWithCollapsingToolbarEnabled = false

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