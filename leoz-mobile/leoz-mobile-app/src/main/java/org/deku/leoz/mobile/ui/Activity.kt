package org.deku.leoz.mobile.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.*
import android.support.transition.*
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.InputType
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.afollestad.materialdialogs.MaterialDialog
import com.github.andrewlord1990.snackbarbuilder.SnackbarBuilder
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.main.*
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.fragment.AidcCameraFragment
import org.slf4j.LoggerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.main_nav_header.view.*
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.prototype.activities.ProtoMainActivity
import org.deku.leoz.mobile.ui.activity.MainActivity
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.view.ActionOverlayView
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.fragment.util.withTransaction
import sx.android.view.setColors
import sx.rx.ObservableRxProperty
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.dev.SyntheticInput
import org.jetbrains.anko.backgroundColor
import sx.aidc.SymbologyType
import sx.android.ApplicationStateMonitor
import sx.android.aidc.SimulatingAidcReader
import sx.android.convertDpToPx
import sx.android.toBitmap
import java.util.NoSuchElementException

/**
 * Leoz activity base class
 * Created by n3 on 23/02/2017.
 */
open class Activity : RxAppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ScreenFragment.Listener,
        ActionOverlayView.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /** Base menu id for dynamically generated synthetic input entries */
        val MENU_ID_DEV_BASE = 0x5000
        val MENU_ID_DEV_MAX = 0x5100
    }

    /** Indicates the activity has been paused */
    private var isPaused = false

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()
    private val applicationStateMonitor: ApplicationStateMonitor by Kodein.global.lazy.instance()

    // AIDC readers
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()
    private val simulatingAidcReader: SimulatingAidcReader by Kodein.global.lazy.instance()

    private val tones: Tones by Kodein.global.lazy.instance()

    // Services
    private val updateService: UpdateService by Kodein.global.lazy.instance()

    // Process models
    private val login: Login by Kodein.global.lazy.instance()

    /** Action items */
    private val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(listOf())
    private var actionItems by actionItemsProperty

    private val actionEventSubject = PublishSubject.create<Int>()
    /** Action overlay event */
    val actionEvent = this.actionEventSubject.hide()

    /** Additional menu items to add */
    var screenMenuItems: Menu? = null

    private val menuItemEventSubject = PublishSubject.create<MenuItem>()
    /** Menu item event */
    val menuItemEvent = this.menuItemEventSubject.hide()

    /** Currently active synthetic inputs */
    private var syntheticInputsProperty = ObservableRxProperty<List<SyntheticInput>>(listOf())
    var syntheticInputs by syntheticInputsProperty

    /** Snackbar builder to use with this activity */
    val snackbarBuilder by lazy {
        SnackbarBuilder(this.uxCoordinatorLayout)
    }

    /**
     * Ref counting progress indicator, wrapping a progress bar
     */
    class ProgressIndicator(
            val progressBar: ProgressBar
    ){
        private var refCount = 0

        fun show() {
            refCount++
            this.progressBar.visibility = View.VISIBLE
        }

        fun hide() {
            if (refCount == 0)
                throw IllegalStateException("Inconsistent show/hide invocations (.hide has been called more often than .show)")

            refCount--
            if (refCount == 0)
                this.progressBar.visibility = View.GONE
        }
    }

    val progressIndicator by lazy { ProgressIndicator(this.uxProgressBar) }

    /**
     * Responsible for controlling header
     */
    private inner class Header {
        /** Default expanded header height */
        private val headerDefaultExpandedSize by lazy {
            this@Activity.resources.getDimension(R.dimen.header_expanded_default_height).toInt()
        }

        /** Default header height (-> action bar height */
        private val headerDefaultHeight by lazy {
            val tv = TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics())
                tv.data
            } else throw NoSuchElementException("Couldnt find attribute actionBarSize")
        }

        /** The default header drawable */
        val defaultDrawable by lazy {
            // Prepare default image
            val sourceImage = this@Activity.baseContext.getDrawable(R.drawable.img_street_1a).toBitmap()

            val bitmap = Bitmap.createBitmap(
                    sourceImage,
                    0,
                    this@Activity.baseContext.convertDpToPx(75F).toInt(),
                    sourceImage.width,
                    this@Activity.baseContext.convertDpToPx(400F).toInt()
            )

            val drawable = BitmapDrawable(this@Activity.resources, bitmap)

            drawable
        }

        /** Header drawable */
        var headerDrawable: Drawable? = null
            set(value) {
                if (field != null) {
                    // Drawable transition animatino
                    val oldDrawable = if (field != null) field else defaultDrawable
                    val newDrawable = if (value != null) value else defaultDrawable

                    val drawable = TransitionDrawable(listOf(
                            oldDrawable,
                            newDrawable
                    ).toTypedArray())

                    this@Activity.uxHeaderImageView.setImageDrawable(drawable)
                    drawable.isCrossFadeEnabled = true
                    drawable.startTransition(500)
                } else {
                    // Don't animate when drawable is set initially
                    this@Activity.uxHeaderImageView.setImageDrawable(value)
                }

                field = value
            }
    }

    /**
     * Header
     */
    private val header = Header()

    // Views

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.main)

        this.uxNavView.setNavigationItemSelectedListener(this)

        //region Progress bar / activity indicator

        // Change progress bar color, as this is apparently not themable and there's no proper
        // way to do this in xml layout that is compatible down to 4.x
        this.uxProgressBar.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorDarkGrey),
                PorterDuff.Mode.SRC_IN);
        //endregion

        //region Action bar
        this.setSupportActionBar(this.uxToolbar)

        val toggle = ActionBarDrawerToggle(
                this,
                this.drawer_layout,
                this.uxToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        this.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //endregion

        // Disable collapsing toolbar title (for now). This will leave regular support action bar title intact.
        this.uxCollapsingToolbarLayout.isTitleEnabled = true

        //region Backstack listener
        this.supportFragmentManager.addOnBackStackChangedListener {
            val fragments = this.supportFragmentManager.fragments ?: listOf<Fragment>()
                    .filterNotNull()
                    .map { it.javaClass.simpleName }

            // Log backstack state
            log.trace("BACKSTACK [${fragments.joinToString(", ")}]")
        }
        //endregion

        //region Keyboard visibility event
        KeyboardVisibilityEvent.setEventListener(
                this,
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (!isOpen) {
                            val transition = Fade(Fade.IN)
                            transition.addTarget(uxActionOverlay)
                            TransitionManager.beginDelayedTransition(uxActionOverlay, transition)
                        }
                        this@Activity.uxActionOverlay.visibility = when (isOpen) { true -> View.GONE; else -> View.VISIBLE
                        }
                    }
                })
        //endregion

        this.uxActionOverlay.fabStyle = R.style.AppTheme_Fab
        this.uxActionOverlay.defaultIcon = R.drawable.ic_chevron_right
        this.uxActionOverlay.defaultIcon = R.drawable.ic_chevron_right
        this.uxActionOverlay.buttonAlpha = 0.87F
        this.uxActionOverlay.listener = this

        this.cameraAidcFragmentVisible = false

        // Register to the app's state change event in order to detect when application comes to foreground
        this.applicationStateMonitor.stateChangedEvent
                .bindToLifecycle(this)
                .filter { it == ApplicationStateMonitor.StateType.Foreground }
                .subscribe {
                    this.onForeground()
                }
    }



    override fun onPause() {
        this.isPaused = true
        this.cameraAidcFragmentVisible = false

        super.onPause()
    }


    override fun onBackPressed() {
        if (this.drawer_layout.isDrawerOpen(GravityCompat.START)) {
            this.drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuInflater.inflate(R.menu.main, menu)

        val mainSubMenu = menu.getItem(0).subMenu

        // Show logout only if there's a user actually logged in
        menu
                .findItem(R.id.action_logout)
                .isVisible = (this.login.authenticatedUser != null)

        menu.setGroupVisible(0, mainSubMenu.hasVisibleItems())

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val mainSubMenu = menu.getItem(0).subMenu

        //region Add screen fragment menu items
        val screenMenuItems = this.screenMenuItems
        if (screenMenuItems != null && screenMenuItems.hasVisibleItems()) {
            for (i in 0..screenMenuItems.size() - 1) {
                val screenMenuItem = screenMenuItems.getItem(i)
                val item = mainSubMenu.add(
                        0,
                        screenMenuItem.itemId,
                        screenMenuItem.order,
                        screenMenuItem.title
                )

                item.icon = screenMenuItem.icon
            }
        }
        //endregion

        //region Add synthetic input entries
        if (this.debugSettings.enabled) {
            for (i in 0..this.syntheticInputs.size - 1) {
                val syntheticInputs = this.syntheticInputs.get(i)
                val item = mainSubMenu.add(
                        0,
                        MENU_ID_DEV_BASE + i,
                        0,
                        syntheticInputs.name
                )

                item.icon = this.getDrawable(R.drawable.ic_barcode_scan)
            }
        }
        //endregion

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        // Handle dynamically generated ids
        if (id >= MENU_ID_DEV_BASE && id < MENU_ID_DEV_MAX) {
            val syntheticInputs = this.syntheticInputs.get(id - MENU_ID_DEV_BASE)

            MaterialDialog.Builder(this)
                    .title(syntheticInputs.name)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .items(*syntheticInputs.entries.map { it.data }.toTypedArray())
                    .itemsCallback { materialDialog, view, i, charSequence ->
                        simulatingAidcReader.emit(data = charSequence.toString(), symbologyType = SymbologyType.Interleaved25)
                    }
                    .cancelable(true)
                    .show()

            return true
        }

        // Handle regular menu entries
        when (id) {
            R.id.action_main -> {
                /** Main submenu entry, ignore */
                return true
            }
            R.id.action_logout -> {
                login.logout()

                this.startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()

                return true
            }

            else -> {
                this.menuItemEventSubject.onNext(item)

                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActionItem(id: Int) {
        // Global action handler
        when (id) {
            R.id.action_aidc_camera -> {
                this.cameraAidcFragmentVisible = !this.cameraAidcFragmentVisible
            }
        }

        // Emit action event
        this.actionEventSubject.onNext(id)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_delivery -> {

            }

            R.id.nav_camera -> {
                // Handle the camera action
            }

            R.id.nav_dev_prototype -> {
                this.startActivity(
                        Intent(applicationContext, ProtoMainActivity::class.java))
            }

            R.id.nav_check_updates -> {
                updateService.trigger()
            }

            R.id.nav_send -> {
            }

            R.id.nav_logout -> {
                this.uxNavView.postDelayed({
                    login.logout()
                    this.startActivity(
                            Intent(applicationContext, MainActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }, 20)
            }
        }

        this.drawer_layout.closeDrawer(GravityCompat.START)

        return false
    }

    private var cameraAidcFragmentVisible: Boolean
        get() {
            val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
            return fragment != null
        }
        set(value) {
            // Lookup dynamically created fab with action overlay for applying translucency effect
            val aidcFab = this.uxActionOverlay.findViewById<FloatingActionButton>(R.id.action_aidc_camera)
            if (aidcFab != null) {
                when (value) {
                    true -> {
                        aidcFab.setColors(backgroundTint = R.color.colorDarkGrey, iconTint = R.color.colorAccent)
                        aidcFab.alpha = 0.6F
                    }
                    false -> {
                        aidcFab.setColors(backgroundTint = R.color.colorDarkGrey, iconTint = android.R.color.white)
                        aidcFab.alpha = 0.85F
                    }
                }
            }

            if (value == this.cameraAidcFragmentVisible)
                return

            if (!value) {
                val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)

                if (fragment != null) {
                    if (isPaused) {
                        // If activitiy is about to pause, avoid animation as they will fail/throw in case activity is detroyed afterwards
                        this@Activity.supportFragmentManager.withTransaction {
                            it.remove(fragment)
                        }
                    } else {
                        val view = fragment.view
                        if (view != null) {
                            // Fragment removal cannot be animated, thus doing it manually
                            ViewCompat.animate(view)
                                    .translationY(view.height.toFloat())
                                    .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                                    .withEndAction {
                                        this@Activity.supportFragmentManager.withTransaction {
                                            it.remove(fragment)
                                        }
                                    }
                                    .start()
                        }

                    }
                }
            } else {
                this.supportFragmentManager.withTransaction {
                    it.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    it.replace(R.id.uxScannerContainer, AidcCameraFragment(), AidcCameraFragment::class.java.canonicalName)
                }
            }
        }

    override fun onResume() {
        super.onResume()

        this.isPaused = false

        // Customize navigation drawer

        val navHeaderView = this.drawer_layout.uxNavView.getHeaderView(0)
        navHeaderView.uxVersion.text = "v${BuildConfig.VERSION_NAME}"

        if (this.debugSettings.enabled) {
            this.uxNavView.menu.findItem(R.id.nav_dev_prototype).setVisible(true)
        }

        this.actionItemsProperty
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    log.info("ACTION ITEMS CAHNGED")
                    val items = mutableListOf(*it.value.toTypedArray())
                    items.add(
                            0,
                            ActionItem(
                                    id = R.id.action_aidc_camera,
                                    colorRes = R.color.colorDarkGrey,
                                    iconTintRes = android.R.color.white,
                                    iconRes = R.drawable.ic_barcode,
                                    visible = this.aidcReader.enabled
                            )
                    )

                    this.uxActionOverlay.items = items
                }

        this.updateService.availableUpdateEvent
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { event ->
                            this.snackbarBuilder
                                    .message(this@Activity.getString(R.string.version_available, event.version))
                                    .duration(Snackbar.LENGTH_INDEFINITE)
                                    .actionText(R.string.update)
                                    .actionClickListener {
                                        event.apk.install(this@Activity)
                                    }
                                    .build().show()
                        })


        // Authentication changes

        this.login.authenticatedUserProperty
                .distinctUntilChanged()
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    this@Activity.invalidateOptionsMenu()

                    val user = it.value
                    when {
                        user != null -> {
                            this.uxNavView.menu
                                    .findItem(R.id.nav_logout)
                                    .setVisible(true)

                            this.uxNavView.menu
                                    .findItem(R.id.nav_dev_prototype)
                                    .setVisible(this.debugSettings.enabled)

                            // Update navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.VISIBLE
                            navHeaderView.uxActiveUser.text = user.email
                            navHeaderView.uxStationID.text = "-_-"
                        }
                        else -> {
                            this.uxNavView.menu
                                    .findItem(R.id.nav_logout)
                                    .setVisible(false)

                            this.uxNavView.menu
                                    .findItem(R.id.nav_dev_prototype)
                                    .setVisible(false)

                            // Hide navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.GONE
                        }
                    }
                }

        //region AIDC
        this.aidcReader.enabledProperty
                .distinctUntilChanged()
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { enabled ->
                    val aidcActionItem = this.uxActionOverlay.items
                            .filter { it.id == R.id.action_aidc_camera && it.visible != enabled.value }
                            .firstOrNull()

                    if (aidcActionItem != null) {
                        aidcActionItem.visible = enabled.value
                        this.uxActionOverlay.update()
                    }
                }

        this.aidcReader.bindActivity(this)

        this.cameraReader.readEvent
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.tones.beep()
                    this.cameraAidcFragmentVisible = false
                }
        //endregion
    }

    /**
     * Shows a screen fragment
     * @param fragment Screen fragment to show
     * @param addToBackStack If the fragment should be added to the backstack
     */
    fun showScreen(fragment: ScreenFragment, addToBackStack: Boolean = true): Int {
        log.trace("SHOW SCREEN [${fragment.javaClass.simpleName}]")

        return supportFragmentManager.withTransaction {
            if (addToBackStack) {
                it.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)

                it.addToBackStack(fragment.javaClass.canonicalName)
            }

            it.replace(this.uxContainer.id, fragment)
        }
    }

    override fun onScreenFragmentResume(fragment: ScreenFragment) {
        // Take over action items from screen fragment when it resumes
        fragment.actionItemsProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribe {
                    this.actionItems = it.value
                }

        fragment.menuProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribeBy(
                        onNext = {
                            this.screenMenuItems = it.value
                            this.invalidateOptionsMenu()
                        },
                        onComplete = {
                            this.screenMenuItems = null
                        }
                )

        fragment.syntheticInputsProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribeBy(
                        onNext = {
                            this.syntheticInputs = it.value
                            this.invalidateOptionsMenu()
                        },
                        onComplete = {
                            this.syntheticInputs = listOf()
                        }
                )

        fragment.accentColorProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribe {
                    this.uxHeaderAccentBar.backgroundColor = ContextCompat.getColor(this, it.value)
                }

        // Setup collapsing layout, appbar & header

        this.uxCollapsingToolbarLayout.title = fragment.title

        var expandAppBar = true
        var scrollCollapseMode = fragment.scrollCollapseMode
        var scroll = (scrollCollapseMode != ScreenFragment.ScrollCollapseModeType.None)

        if (fragment.headerImage == 0) {
            expandAppBar = false
            scroll = true
        }

        log.trace("HEADER HEIGHT ${this.uxHeader.layoutParams.height}")

        if (fragment.hideActionBar) {
            // Workaround for supportActionBar not adjusting content area

            // Hiding the entire appbar via expanded flag only works in conjunction
            // with collapsing toolbar scroll/snap mode
            expandAppBar = false
            scroll = true
            scrollCollapseMode = ScreenFragment.ScrollCollapseModeType.EnterAlwaysCollapsed
        }

        // Apply action bar changes
        run {
            // Make sure app bar is visible (eg. when screen changes)
            // otherwise transitioning from a scrolling to a static content screen
            // may leave the app bar hidden.
            log.trace("APPBAR EXPAND ${expandAppBar}")

            this.uxAppBarLayout.setExpanded(expandAppBar, true)
        }

        // Apply header changes
        run {
            // TODO: don't expand when scroll position is not top on pre-existing fragment

            this.header.headerDrawable = if (fragment.headerImage != 0)
                ContextCompat.getDrawable(baseContext, fragment.headerImage)
            else
                this.header.defaultDrawable
        }

        // Apply collapsing toolbar settings
        this.uxCollapsingToolbarLayout.post {
            // EXIT_UNTIL_COLLAPSED should always be the default, so title and appbar expansion works properly
            val collapsingScrollFlag = when (scrollCollapseMode) {
                ScreenFragment.ScrollCollapseModeType.ExitUntilCollapsed -> AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                ScreenFragment.ScrollCollapseModeType.EnterAlways -> AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                ScreenFragment.ScrollCollapseModeType.EnterAlwaysCollapsed -> AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED

                ScreenFragment.ScrollCollapseModeType.None -> AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                else -> 0
            }

            val scrollFlag = when (scroll) {
                false -> 0
                else -> AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            }

            val scrollSnapFlag = when (fragment.scrollSnap) {
                true -> AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                false -> 0
            }

            val layoutParams = this.uxCollapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams

            layoutParams.scrollFlags =
                    scrollFlag or collapsingScrollFlag or scrollSnapFlag

            log.trace("SCROLL FLAGS ${layoutParams.scrollFlags}")

            this.uxCollapsingToolbarLayout.requestLayout()
        }

        // Apply requested orientation
        this.requestedOrientation = when {
            debugSettings.userScreenRotation -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // Enforce orientation on every fragment resume
            else -> fragment.orientation
        }

        this.aidcReader.enabled = fragment.aidcEnabled
    }

    override fun onScreenFragmentPause(fragment: ScreenFragment) {
        this.cameraAidcFragmentVisible = false
    }

    /**
     * On foreground handler (hooked into ApplicationStateMonitor)
     */
    private fun onForeground() {
        // Check developer settings
        if (!debugSettings.enabled && Settings.Secure.getString(this.contentResolver, Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED) == "1") {
            MaterialDialog.Builder(this)
                    .title("Developer options enabled")
                    .content("Developer options are enabled on your device. To continue, you must disable developer options!")
                    .positiveText("Settings")
                    .negativeText("Abort")
                    .onPositive { materialDialog, dialogAction ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        this.startActivityForResult(intent, 0)
                    }
                    .onNegative { materialDialog, dialogAction ->
                        when (android.os.Build.VERSION.SDK_INT) {
                            android.os.Build.VERSION_CODES.LOLLIPOP -> this.finishAndRemoveTask()
                            else -> this.finishAffinity()
                        }
                    }
                    .cancelable(false)
                    .show()
        }
    }
}