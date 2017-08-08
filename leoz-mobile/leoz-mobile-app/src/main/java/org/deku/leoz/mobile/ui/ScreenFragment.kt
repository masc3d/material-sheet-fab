package org.deku.leoz.mobile.ui

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.Menu
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.ui.view.ActionItem
import sx.rx.ObservableRxProperty

/**
 * Created by masc on 14.06.17.
 */
open class ScreenFragment<P> : Fragment<P>() {
    val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(default = listOf())
    /** Screen action items */
    var actionItems by actionItemsProperty

    val menuProperty by lazy { ObservableRxProperty<Menu?>(null) }
    /** Screen menu items */
    var menu: Menu? by menuProperty

    val accentColorProperty by lazy { ObservableRxProperty<Int>(R.color.colorAccent) }
    var accentColor by accentColorProperty
        @ColorRes get

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

    var syntheticInputsProperty = ObservableRxProperty<List<SyntheticInput>>(listOf())
    /**
     * A list of synthetic input specifications.
     */
    var syntheticInputs by syntheticInputsProperty

    interface Listener {
        fun onScreenFragmentResume(fragment: ScreenFragment<*>) {}
        fun onScreenFragmentPause(fragment: ScreenFragment<*>) {}
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