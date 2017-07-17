package org.deku.leoz.mobile.ui

import android.content.pm.ActivityInfo
import android.support.annotation.DrawableRes
import android.view.Menu
import org.deku.leoz.mobile.ui.view.ActionItem
import sx.rx.ObservableRxProperty

/**
 * Created by masc on 14.06.17.
 */
open class ScreenFragment : Fragment() {
    val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(listOf())
    /** Screen action items */
    var actionItems by actionItemsProperty

    val menuProperty by lazy { ObservableRxProperty<Menu?>(null) }
    /** Screen menu items */
    var menu: Menu? by menuProperty

    /**
     * Title
     */
    var title: String = ""

    enum class ScrollCollapseModeType {
        None,
        ExitUntilCollapsed,
        EnterAlways,
        EnterAlwaysCollapsed
    }

    /**
     * Enables collapsing toolbar's scroll/snap mode.
     */
    var scrollCollapseMode: ScrollCollapseModeType = ScrollCollapseModeType.None

    /**
     * Enable/disable scroll snap
     */
    var scrollSnap = false

    /**
     * One of ActivityInfo.SCREEN_ORIENTATION_*
     */
    var orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    /**
     * Screen should hide action bar
     */
    var hideActionBar: Boolean = false

    /**
     * Header image
     */
    var headerImage: Int = 0
        @DrawableRes get

    /** The initial aidc reader setting for this screen */
    var aidcEnabled: Boolean = false

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