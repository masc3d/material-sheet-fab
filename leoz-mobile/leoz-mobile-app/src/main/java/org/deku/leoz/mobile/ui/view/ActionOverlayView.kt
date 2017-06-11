package org.deku.leoz.mobile.ui.view

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_actionoverlay.*
import org.deku.leoz.mobile.R
import android.view.Gravity
import android.support.v7.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gordonwong.materialsheetfab.MaterialSheetFab
import kotlinx.android.synthetic.main.view_actionoverlay.view.*
import kotlinx.android.synthetic.main.view_actionoverlay_sheet.view.*
import kotlinx.android.synthetic.main.view_actionoverlay_sheet_item.view.*
import org.jetbrains.anko.itemsSequence
import org.jetbrains.anko.layoutInflater
import sx.android.view.setBackgroundTint


/**
 * Created by masc on 08.06.17.
 */
class ActionOverlayView : RelativeLayout {

    /**
     * Action overlay item/floating button
     * @property id Action id of this item
     * @property colorRes Item color (fab background color eg.)
     * @property iconRes Icon
     * @property iconTintRes Color resource for tinting the icon
     * @property menu Menu reflecting subitems
     */
    data class Item(
            val id: Int,
            val colorRes: Int? = null,
            val iconRes: Int? = null,
            val iconTintRes: Int? = null,
            val menu: Menu? = null
    )

    interface Listener {
        fun onActionItem(id: Int)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    /** Resource id of style to use for dynamically created fabs */
    var fabStyle: Int = 0

    /** The overlay view to use when dimming */
    lateinit var overlayView: View

    var listener: Listener? = null

    /**
     * Action items
     */
    var items: List<Item> = listOf()
        set(value) {
            field = value
            this.update()
        }

    /**
     * Dynamically created material sheet fabs
     */
    var materialSheetFabs = mutableMapOf<Int, MaterialSheetFab<AnimatedFloatingActionButton>>()

    private fun init() {
        inflate(this.context, R.layout.view_actionoverlay, this)
        this.overlayView = this.actionoverlay_dim
    }

    /**
     * Setup floating action button parameters for action layout
     */
    private fun FloatingActionButton.setup(): FloatingActionButton {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        params.gravity = Gravity.BOTTOM

        this.layoutParams = params

        return this
    }

    /**
     * Create floating action button
     */
    private fun createFab(): FloatingActionButton {
        return FloatingActionButton(
                ContextThemeWrapper(this.context, this.fabStyle)
        )
                .setup()
    }

    /**
     * Create animated floating action button
     */
    private fun createAnimatedFab(): AnimatedFloatingActionButton {
        return AnimatedFloatingActionButton(
                ContextThemeWrapper(this.context, this.fabStyle)
        )
                .setup() as AnimatedFloatingActionButton
    }

    fun update() {
        // Remove fabs
        this.actionoverlay_container.removeAllViews()
        // Remove fab sheets
        this.actionoverlay_sheet_container.removeAllViews()

        this.materialSheetFabs.clear()

        this.items.forEach { item ->

            when {
                item.menu == null -> {
                    // Create regular fab

                    val fab = this.createFab()
                    fab.id = item.id

                    if (item.colorRes != null)
                        fab.setBackgroundTint(item.colorRes)

                    if (item.iconRes != null)
                        fab.setImageDrawable(this.context.getDrawable(item.iconRes))

                    fab.setOnClickListener {
                        this.listener?.onActionItem(item.id)
                    }

                    this.actionoverlay_container.addView(fab)
                }
                else -> {
                    // Create animated fab and material sheet

                    val fab = this.createAnimatedFab()

                    val sheetLayout = this.context.layoutInflater.inflate(
                            R.layout.view_actionoverlay_sheet,
                            this.actionoverlay_sheet_container)

                    // Create sheet items
                    item.menu.itemsSequence().forEach { menuItem ->
                        val sheetItem = this.context.layoutInflater.inflate(
                                R.layout.view_actionoverlay_sheet_item,
                                sheetLayout.actionoverlay_sheet_item_container,
                                false)

                        sheetItem.actionoverlay_sheet_item_icon.setImageDrawable(menuItem.icon)
                        sheetItem.actionoverlay_sheet_item_title.setText(menuItem.title)

                        sheetLayout.actionoverlay_sheet_item_container.addView(sheetItem)
                    }

                    val sheetColor = resources.getColor(android.R.color.white)
                    val fabColor = resources.getColor(item.colorRes ?: R.color.colorAccent)
                    val msf = MaterialSheetFab<AnimatedFloatingActionButton>(
                            fab,
                            sheetLayout.actionoverlay_sheet,
                            this.overlayView,
                            sheetColor,
                            // TODO: don't reference app theme
                            fabColor)


                    // Material sheet fabs are not directly added to view hierarchy,
                    // but used for controlling behavior. Storing internally.
                    this.materialSheetFabs.put(item.id, msf)

                    this.actionoverlay_container.addView(fab)
                }
            }
        }
    }
}