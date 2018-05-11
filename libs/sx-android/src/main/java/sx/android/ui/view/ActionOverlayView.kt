package sx.android.ui.view

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StyleRes
import android.support.design.widget.FloatingActionButton
import android.support.transition.Explode
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import sx.android.R
import sx.android.rx.observeOnMainThread
import java.util.concurrent.TimeUnit

/**
 * Action overlay item/floating button
 * @property id Action id of this item
 * @property colorRes Item color (fab background color eg.)
 * @property iconRes Icon
 * @property iconTintRes Color resource for tinting the icon
 * @property alpha Action item alpha
 * @property alignEnd Align action item at end of parent. Defaults to true.
 * @property visible Visibility of action item
 * @property menu Menu reflecting subitems
 */
data class ActionItem(
        @IdRes val id: Int,
        @ColorRes var colorRes: Int? = null,
        @DrawableRes val iconRes: Int? = null,
        @ColorRes var iconTintRes: Int? = null,
        val alpha: Float? = null,
        val alignEnd: Boolean = true,
        var visible: Boolean = true,

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

    private enum class SheetState {
        HIDDEN,
        SHOWN,
        HIDING,
        SHOWING
    }

    /** Resource id of style to use for dynamically created fabs */
    @StyleRes
    var fabStyle: Int = 0
        set(value) {
            field = value
        }

    @DrawableRes
    var defaultIcon: Int = 0
        set(value) {
            field = value
        }

    @DrawableRes
    var defaultIconTint: Int = 0
        set(value) {
            field = value
        }

    /** The overlay view to use when dimming */
    lateinit var overlayView: View

    var listener: Listener? = null

    /** Default button alpha */
    var buttonAlpha: Float = 1.0F

    /**
     * Action items
     */
    var items: List<ActionItem> = listOf()
        set(value) {
            field = value
            this.itemsSubject.onNext(value)
        }

    private val itemsSubject = PublishSubject.create<List<ActionItem>>()

    /** Debounced items observable, avoids unnecessary re-layout when items are assigned
     * multiple times during a single ui loop */
    private val backpressuredItems = itemsSubject
            .debounce(50, TimeUnit.MILLISECONDS)

    init {
        this.backpressuredItems
                .observeOnMainThread()
                .subscribe {
                    this.update()
                }
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
            TransitionManager.beginDelayedTransition(this.uxActionOverlayContainer, Explode().apply {
                duration = 200
            })

            // Remove fabs
            this.uxActionOverlayContainerStart.removeAllViews()
            this.uxActionOverlayContainerEnd.removeAllViews()
            // Remove fab sheets
            this.uxActionOverlaySheetContainer.removeAllViews()

            this.materialSheetFabs.clear()

            val visibleItems = this.items.filter {
                if (it.menu == null)
                    it.visible
                else
                    it.visible && it.menu.itemsSequence().any { it.isVisible }
            }

            visibleItems
                    .filter { it.alignEnd }
                    .reversed()
                    .plus(visibleItems.filter { it.alignEnd == false })
                    .forEach { item ->

                        val fab: FloatingActionButton
                        when {
                            item.menu == null -> {
                                // Create regular fab
                                fab = this.createFab()

                                fab.setOnClickListener {
                                    this.listener?.onActionItem(item.id)
                                }
                            }
                            else -> {
                                // Create animated fab and material sheet
                                val animatedFab = this.createAnimatedFab()
                                fab = animatedFab

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
                                val sheetBackgroundColor = ContextCompat.getColor(this.context, android.R.color.background_light)
                                val colorRes = item.colorRes
                                val fabColor =
                                        if (colorRes != null)
                                            ContextCompat.getColor(this.context, colorRes)
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

                                // Set sheet color
                                sheet.uxAtionOverlaySheetBar.setBackgroundColor(fabColor)

                                // Create sheet items
                                item.menu.itemsSequence()
                                        .filter { it.isEnabled && it.isVisible }
                                        .forEach { menuItem ->

                                            val sheetItem = this.context.layoutInflater.inflate(
                                                    R.layout.view_actionoverlay_sheet_item,
                                                    sheet.uxActionOverlaySheetItemContainer,
                                                    false)

                                            sheetItem.uxActionOverlaySheetItemIcon.setImageDrawable(
                                                    menuItem.icon
                                                    // Set default icon if applicable
                                                            ?: (if (this.defaultIcon != 0) ContextCompat.getDrawable(context, this.defaultIcon) else null)
                                            )

                                            sheetItem.uxActionOverlaySheetItemTitle.text = menuItem.title

                                            // masc20170811. Putting this observable sinde the onClickListener lambda
                                            // will cause release builds to fail (on proguard) with androidstudio-2.3.3
                                            val ovSheetHidden = sheetFab.event
                                                    .takeUntil { it == SheetState.HIDDEN }

                                            sheetItem.setOnClickListener {
                                                sheetFab.value.hideSheet()

                                                // masc20170811. workaround for fab button vanishing when onclick messes with the view hierarchy
                                                // eg. showing a material dialog seems to interrupt material sheet hide animations
                                                // and fab button will not be restored to visible state after sheet has closed
                                                ovSheetHidden.subscribeBy(onComplete = {
                                                    // TODO: when listener/activity is destroyed while hide sheet/animation has not finished, the view hierarchy including context/activity will leak. Commenting this. Commenting .hideSheet avoids the leak
                                                    this.listener?.onActionItem(menuItem.itemId)
                                                })
                                            }

                                            sheet.uxActionOverlaySheetItemContainer.addView(sheetItem)
                                        }

                                fab.setOnClickListener {
                                    this.materialSheetFabs.values
                                            .filter { it.value != sheetFab.value && it.value.isSheetVisible }
                                            .forEach { it.value.hideSheet() }

                                    sheetFab.value.showSheet()
                                }
                            }
                        }

                        fab.id = item.id

                        item.colorRes?.also {
                            fab.backgroundTintList = ContextCompat.getColorStateList(this.context, it)
                        }

                        if (item.iconRes != null)
                            fab.setImageDrawable(ContextCompat.getDrawable(this.context, item.iconRes))

                        item.iconTintRes?.also {
                            fab.setIconTintRes(it)
                        }

                        fab.alpha = item.alpha ?: this.buttonAlpha

                        if (item.alignEnd)
                            this.uxActionOverlayContainerEnd.addView(fab)
                        else
                            this.uxActionOverlayContainerStart.addView(fab)
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
            log.trace("No visible sheets")
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
                                log.trace("All sheets in hidden state")
                                // Update when all sheets went into HIDDEN state
                                updateImpl.invoke()
                            })
        }
    }
}