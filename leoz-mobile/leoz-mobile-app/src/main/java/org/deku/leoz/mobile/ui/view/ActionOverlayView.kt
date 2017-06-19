package org.deku.leoz.mobile.ui.view

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StyleRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import org.deku.leoz.mobile.R
import android.view.Gravity
import android.support.v7.view.ContextThemeWrapper
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.gordonwong.materialsheetfab.DimOverlayFrameLayout
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_actionoverlay.view.*
import kotlinx.android.synthetic.main.view_actionoverlay_sheet.view.*
import kotlinx.android.synthetic.main.view_actionoverlay_sheet_item.view.*
import org.jetbrains.anko.itemsSequence
import org.jetbrains.anko.layoutInflater
import org.slf4j.LoggerFactory
import sx.android.view.setBackgroundTint
import sx.android.view.setIconTint

/**
 * Action overlay item/floating button
 * @property id Action id of this item
 * @property colorRes Item color (fab background color eg.)
 * @property iconRes Icon
 * @property iconTintRes Color resource for tinting the icon
 * @property menu Menu reflecting subitems
 */
data class ActionItem(
        @IdRes val id: Int,
        @ColorRes val colorRes: Int? = null,
        @DrawableRes val iconRes: Int? = null,
        @ColorRes val iconTintRes: Int? = null,
        val menu: Menu? = null
)

/**
 * An overlay with support for dynamically configuring actions and menus
 * which are displayed as floating action buttons and sheet fabs accordingly
 * Created by masc on 08.06.17.
 */
class ActionOverlayView : RelativeLayout {
    private val log = LoggerFactory.getLogger(this.javaClass)

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

    private enum class SheetState {
        HIDDEN,
        SHOWN,
        HIDING,
        SHOWING
    }

    /** Resource id of style to use for dynamically created fabs */
    var fabStyle: Int = 0
        @StyleRes
        set(value) {
            field = value
        }

    var defaultIcon: Int = 0
        @DrawableRes
        set(value) {
            field = value
        }

    var defaultIconTint: Int = 0
        @DrawableRes
        set(value) {
            field = value
        }

    /** The overlay view to use when dimming */
    lateinit var overlayView: View

    var listener: Listener? = null

    /**
     * Action items
     */
    var items: List<ActionItem> = listOf()
        set(value) {
            field = value
            this.update()
        }

    /**
     * Registers to the material sheet fab's event handler and returns a subject reflecting those events
     */
    private fun MaterialSheetFab<AnimatedFloatingActionButton>.createEventSubject(): PublishSubject<SheetState> {
        val subject = PublishSubject.create<SheetState>()

        this.setEventListener(object : MaterialSheetFabEventListener() {
            override fun onSheetHidden() {
                subject.onNext(SheetState.HIDDEN)
            }

            override fun onSheetShown() {
                subject.onNext(SheetState.SHOWN)
            }

            override fun onShowSheet() {
                subject.onNext(SheetState.SHOWING)
            }

            override fun onHideSheet() {
                subject.onNext(SheetState.HIDING)
            }
        })

        return subject
    }

    inner private class SheetFab(
            val value: MaterialSheetFab<AnimatedFloatingActionButton>
    ) {
        val event: Observable<SheetState> = value.createEventSubject()
        /** Last sheet state */
        var state: SheetState = SheetState.HIDDEN

        init {
            // Subscribe to maintain internal state
            this.event.subscribe {
                log.trace("Sheet [${value}] changed to [${it}]")
                this.state = it
            }
        }
    }

    /**
     * Dynamically created material sheet fabs
     */
    private var materialSheetFabs = mutableMapOf<Int, SheetFab>()

    private fun init() {
        inflate(this.context, R.layout.view_actionoverlay, this)
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
        val updateImpl = {
            // Remove fabs
            this.uxActionOverlayContainer.removeAllViews()
            // Remove fab sheets
            this.uxActionOverlaySheetContainer.removeAllViews()

            this.materialSheetFabs.clear()

            this.items.reversed().forEach { item ->

                when {
                    item.menu == null -> {
                        // Create regular fab

                        val fab = this.createFab()
                        fab.id = item.id

                        if (item.colorRes != null)
                            fab.setBackgroundTint(item.colorRes)

                        if (item.iconRes != null)
                            fab.setImageDrawable(ContextCompat.getDrawable(this.context, item.iconRes))

                        if (item.iconTintRes != null)
                            fab.setIconTint(item.iconTintRes)

                        fab.setOnClickListener {
                            this.listener?.onActionItem(item.id)
                        }

                        this.uxActionOverlayContainer.addView(fab)
                    }
                    else -> {
                        // Create animated fab and material sheet

                        val fab = this.createAnimatedFab()

                        fab.id = item.id

                        if (item.colorRes != null)
                            fab.setBackgroundTint(item.colorRes)

                        if (item.iconRes != null)
                            fab.setImageDrawable(ContextCompat.getDrawable(this.context, item.iconRes))

                        if (item.iconTintRes != null)
                            fab.setIconTint(item.iconTintRes)

                        val sheet = this.context.layoutInflater.inflate(
                                R.layout.view_actionoverlay_sheet,
                                this.uxActionOverlaySheetContainer,
                                false)

                        this.uxActionOverlaySheetContainer.addView(sheet)

                        // Create dedicated dim overlay for this sheet
                        val dimOverlay = DimOverlayFrameLayout(this.context)

                        dimOverlay.layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT)

                        this.uxActionOverlayDimContainer.addView(dimOverlay)

                        // Create matieral sheet fab
                        val sheetBackgroundColor = resources.getColor(android.R.color.background_light)
                        val fabColor =
                                if (item.colorRes != null)
                                    resources.getColor(item.colorRes)
                                else
                                // Default to fab color
                                    fab.backgroundTintList!!.defaultColor

                        val sheetFab = SheetFab(
                                MaterialSheetFab<AnimatedFloatingActionButton>(
                                        fab,
                                        sheet.uxActionOverlaySheet,
                                        dimOverlay,
                                        sheetBackgroundColor,
                                        fabColor)
                        )

                        // Material sheet fabs are not directly added to view hierarchy,
                        // but used for controlling behavior. Storing internally.
                        this.materialSheetFabs.put(item.id, sheetFab)
                        log.info("FU ${materialSheetFabs.count()}")

                        // Set sheet color
                        sheet.uxAtionOverlaySheetBar.setBackgroundColor(fabColor)

                        // Create sheet items
                        item.menu.itemsSequence().forEach { menuItem ->
                            val sheetItem = this.context.layoutInflater.inflate(
                                    R.layout.view_actionoverlay_sheet_item,
                                    sheet.uxActionOverlaySheetItemContainer,
                                    false)

                            sheetItem.uxActionOverlaySheetItemIcon.setImageDrawable(
                                    menuItem.icon
                                            // Set default icon if applicable
                                            ?: (if (this.defaultIcon != 0) ContextCompat.getDrawable(context, this.defaultIcon) else null)
                            )

                            sheetItem.uxActionOverlaySheetItemTitle.setText(menuItem.title)

                            sheetItem.setOnClickListener {
                                sheetFab.value.hideSheet()
                                this.listener?.onActionItem(menuItem.itemId)
                            }

                            sheet.uxActionOverlaySheetItemContainer.addView(sheetItem)
                        }

                        fab.setOnClickListener {
                            this.materialSheetFabs.values
                                    .filter { it.value != sheetFab.value && it.value.isSheetVisible }
                                    .forEach { it.value.hideSheet() }

                            sheetFab.value.showSheet()
                        }

                        this.uxActionOverlayContainer.addView(fab)
                    }
                }
            }
        }

        // Prevent clearing out views which have animations in-flight
        // TODO: as soon as properly fixed in material-sheet-fab, coordination may become obsolete

        // Check for visible sheet fabs
        val visibleSheetFabs = this.materialSheetFabs.values
                .filter {
                    when (it.state) {
                        SheetState.SHOWN, SheetState.SHOWING, SheetState.HIDING -> true
                        else -> false
                    }
                }
                .toList()

        if (visibleSheetFabs.count() == 0) {
            log.info("No visible sheets")
            // No sheets visible, update immediately
            updateImpl.invoke()
        } else {
            // Hide all sheets, just to make sure
            visibleSheetFabs.forEach {
                it.value.hideSheet()
            }

            // Coordinate with animations/material sheet fab event handler
            Observable.zip(
                    // Create observable from event subject, completing on next HIDDEN event
                    visibleSheetFabs.map {
                        it.event.filter { it == SheetState.HIDDEN }
                    },
                    // Zip all event handlers
                    {
                        true
                    }
            )
                    .take(1)
                    .subscribeBy(
                            onComplete = {
                                log.info("All sheets in hidden state")
                                // Update when all sheets went into HIDDEN state
                                updateImpl.invoke()
                            })
        }
    }
}