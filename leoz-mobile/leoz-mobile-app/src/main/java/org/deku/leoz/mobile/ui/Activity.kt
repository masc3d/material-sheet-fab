package org.deku.leoz.mobile.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.text.InputType
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.afollestad.materialdialogs.MaterialDialog
import com.github.andrewlord1990.snackbarbuilder.SnackbarBuilder
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.main_nav_header.view.*
import kotlinx.android.synthetic.main.view_update_indicator.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.databinding.ViewConnectivityIndicatorBinding
import org.deku.leoz.mobile.databinding.ViewMqIndicatorBinding
import org.deku.leoz.mobile.databinding.ViewUpdateIndicatorBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.mq.MimeType
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.mq.sendFile
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.activity.MainActivity
import org.deku.leoz.mobile.ui.activity.StartupActivity
import org.deku.leoz.mobile.ui.fragment.AidcCameraFragment
import org.deku.leoz.mobile.ui.screen.BaseCameraScreen
import org.deku.leoz.mobile.ui.screen.CameraScreen
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.view.ActionOverlayView
import org.deku.leoz.mobile.ui.vm.ConnectivityViewModel
import org.deku.leoz.mobile.ui.vm.MqStatisticsViewModel
import org.deku.leoz.mobile.ui.vm.UpdateServiceViewModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.locationManager
import org.slf4j.LoggerFactory
import sx.aidc.SymbologyType
import sx.android.ApplicationStateMonitor
import sx.android.Connectivity
import sx.android.Device
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.aidc.SimulatingAidcReader
import sx.android.fragment.util.withTransaction
import sx.android.isConnectivityProblem
import sx.android.rx.observeOnMainThread
import sx.android.view.setIconTintRes
import sx.mq.mqtt.MqttDispatcher
import sx.mq.mqtt.channel
import sx.rx.ObservableRxProperty
import java.util.*

/**
 * Leoz activity base class
 * Created by n3 on 23/02/2017.
 */
abstract class Activity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ScreenFragment.Listener,
        ActionOverlayView.Listener,
        BaseCameraScreen.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /** Base menu id for dynamically generated synthetic input entries */
        val MENU_ID_DEV_BASE = 0x5000
        val MENU_ID_DEV_MAX = 0x5100

        val AIDC_ACTION_ITEM_COLOR = R.color.colorDarkGrey
        val AIDC_ACTION_ITEM_TINT = android.R.color.white
    }

    /** Indicates the activity has been paused */
    private var isPaused = false

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()
    private val remoteSettings: RemoteSettings by Kodein.global.lazy.instance()
    private val applicationStateMonitor: ApplicationStateMonitor by Kodein.global.lazy.instance()

    private val device: Device by Kodein.global.lazy.instance()
    private val identity: Identity by Kodein.global.lazy.instance()

    // AIDC readers
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()
    private val simulatingAidcReader: SimulatingAidcReader by Kodein.global.lazy.instance()

    private val tones: Tones by Kodein.global.lazy.instance()

    // Services
    private val updateService: UpdateService by Kodein.global.lazy.instance()

    // Process models
    private val login: Login by Kodein.global.lazy.instance()

    private val connectivity: Connectivity by Kodein.global.lazy.instance()

    private val mqttDispatcher: MqttDispatcher by Kodein.global.lazy.instance()
    private val mqttEndpoints: MqttEndpoints by Kodein.global.lazy.instance()

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

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        mqttEndpoints.central.main.channel().sendFile(jpeg, MimeType.JPEG.value)
    }

    /**
     * Ref counting progress indicator, wrapping a progress bar
     */
    inner class ProgressIndicator(
            val progressBar: ProgressBar
    ) {
        private var refCount = 0

        fun show() {
            refCount++
            this.progressBar.post {
                this.progressBar.visibility = View.VISIBLE
            }
        }

        fun hide() {
            if (refCount == 0)
                throw IllegalStateException("Inconsistent show/hide invocations (.hide has been called more often than .show)")

            refCount--
            if (refCount == 0)
                this.progressBar.post {
                    this.progressBar.visibility = View.GONE
                }
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
            val tv = TypedValue()
            if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                tv.data
            } else throw NoSuchElementException("Couldnt find attribute actionBarSize")
        }

        /** The default header drawable */
        val defaultDrawable
            get() = Headers.street

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

        if (!this.app.isInitialized) {
            // Currently the startup activity is required for seamless startup
            // as it takes care of synchronously retrieving the honeywell aidc reader from the
            // aidc manager service. Without a main thread handler cycle eg. activity transition,
            // injecting the aidc instance (on main thread) will cause a deadlock
            // TODO: HoneywellAidcReader internals should be completely reactive, so consumers don't have to worry about this.

            // Use the activity task state to determine the most recent active
            // activity, so we can (at least) restore the most recent activity for the user.
            val i = Intent(this, StartupActivity::class.java).also {
                it.putExtra("ACTIVITY", this.javaClass.canonicalName)
            }
            this.startActivity(i)

            this.finish()
            return
        }

        this.setContentView(R.layout.main)

        //region Manual bindings
        DataBindingUtil.bind<ViewUpdateIndicatorBinding>(this.uxUpdateIndicator).also {
            it.updateService = UpdateServiceViewModel(this.updateService)
        }

        DataBindingUtil.bind<ViewMqIndicatorBinding>(this.uxMqIndicator).also {
            it.mqStatistics = MqStatisticsViewModel(
                    mqttDispatcher = this.mqttDispatcher,
                    mqttEndpoints = this.mqttEndpoints
            )
        }

        DataBindingUtil.bind<ViewConnectivityIndicatorBinding>(this.uxConnectivityIndicator).also {
            it.connectivity = ConnectivityViewModel(this.connectivity)
        }
        //endregion

        //region Progress bar / activity indicator
        this.uxProgressBar.visibility = View.GONE

        // Change progress bar color, as this is apparently not themable and there's no proper
        // way to do this in xml layout that is compatible down to 4.x
        // TODO: this rather hacky solution should be removed when API level 21 is minimum, which supports `indeterminateTint` attributes
        this.uxProgressBar.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorDarkGrey),
                PorterDuff.Mode.SRC_IN)

        this.uxUpdateIndicator.uxUpdateProgressBar.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorGrey),
                PorterDuff.Mode.SRC_IN)
        //endregion

        this.uxNavView.setNavigationItemSelectedListener(this)

        //region Action bar
        this.setSupportActionBar(this.uxToolbar)

        val toggle = ActionBarDrawerToggle(
                this,
                this.uxDrawerLayout,
                this.uxToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        this.uxDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //endregion

        // Disable collapsing toolbar title (for now). This will leave regular support action bar title intact.
        this.uxCollapsingToolbarLayout.isTitleEnabled = true

        //region Backstack listener
        this.supportFragmentManager.addOnBackStackChangedListener {
            val fragments = this.supportFragmentManager.fragments ?: listOf<Fragment<*>>()
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
        if (this.uxDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.uxDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuInflater.inflate(R.menu.main, menu)

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
        if (this.debugSettings.syntheticAidcEnabled) {
            for (i in 0..this.syntheticInputs.size - 1) {
                val syntheticInputs = this.syntheticInputs.get(i)
                val item = mainSubMenu.add(
                        0,
                        MENU_ID_DEV_BASE + i,
                        0,
                        syntheticInputs.name
                )

                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_barcode_scan)
            }
        }
        //endregion

        menu.setGroupVisible(0, mainSubMenu.hasVisibleItems())

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
                    .itemsCallback { _, _, _, charSequence ->
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
                if (!this.cameraAidcFragmentVisible)
                    this.uxAppBarLayout.setExpanded(false, true)
            }

            R.id.action_aidc_keyboard -> {
                this.cameraAidcFragmentVisible = false

                MaterialDialog.Builder(this)
                        .title(R.string.manual_label_input)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(R.string.barcode_label, 0, object : MaterialDialog.InputCallback {
                            override fun onInput(dialog: MaterialDialog, input: CharSequence?) {
                                log.trace("MANUAL INPUT ${input}")
                                this@Activity.simulatingAidcReader.emit(
                                        input.toString(),
                                        SymbologyType.Unknown
                                )
                            }
                        })
                        .build()
                        .show()
            }

        }

        // Emit action event
        this.actionEventSubject.onNext(id)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_camera -> {
                // Handle the camera action
                this.showScreen(CameraScreen())
            }

            R.id.nav_dev_remote_settings -> {
                MaterialDialog.Builder(this)
                        .content("Remote settings:\n${remoteSettings}\n\nDebug settings:\n${debugSettings}")
                        //.content("Debug settings:\n" + debugSettings.toString())
                        .cancelable(true)
                        .show()
            }

            R.id.nav_check_updates -> {
                updateService.trigger()
            }

            R.id.nav_send -> {
            }

            R.id.nav_logout -> {
                this.login.logout()
            }
        }

        this.uxDrawerLayout.closeDrawer(GravityCompat.START)

        return false
    }

    private val cameraAidcFragment: AidcCameraFragment?
        get() = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName) as? AidcCameraFragment

    /**
     * Aidc fragment control
     */
    private var cameraAidcFragmentVisible: Boolean
        get() = this.cameraAidcFragment != null
        set(value) {
            if (value == this.cameraAidcFragmentVisible)
                return

            when (value) {
                true -> {
                    // Show camera fragment
                    this.supportFragmentManager.withTransaction {
                        it.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        it.replace(R.id.uxScannerContainer, AidcCameraFragment(), AidcCameraFragment::class.java.canonicalName)
                    }

                    // Remove action items,add aidc keyboard button whie camera fragment is visilble
                    this.actionItems = listOf(
                            ActionItem(
                                    id = R.id.action_aidc_keyboard,
                                    colorRes = AIDC_ACTION_ITEM_COLOR,
                                    iconRes = R.drawable.ic_keyboard,
                                    iconTintRes = AIDC_ACTION_ITEM_TINT
                            )
                    )
                }

                false -> {
                    val fragment = this.cameraAidcFragment

                    if (fragment != null) {
                        if (isPaused) {
                            // If activitiy is about to pause, avoid animation as they will fail/throw in case activity is detroyed afterwards
                            this@Activity.supportFragmentManager.withTransaction {
                                it.remove(fragment)
                            }
                        } else {
                            val view = fragment.view
                            if (view != null) {
                                // Fragment removal cannot be animated with custom animation, thus doing it manually
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

                        // Find top-most screen fragment and restore action items
                        val screenFragment = this.supportFragmentManager.fragments
                                .firstOrNull {
                                    it is ScreenFragment<*>
                                } as? ScreenFragment<*>

                        if (screenFragment != null)
                            this.actionItems = screenFragment.actionItems
                    }
                }
            }

            // Lookup dynamically created fab with action overlay for applying translucency effect
            listOf(
                    this.uxActionOverlay.findViewById<FloatingActionButton>(R.id.action_aidc_camera),
                    this.uxActionOverlay.findViewById<FloatingActionButton>(R.id.action_aidc_keyboard)
            )
                    .filterNotNull()
                    .forEach {

                        when (value) {
                            true -> {
                                if (it.id == R.id.action_aidc_camera)
                                    it.setIconTintRes(R.color.colorAccent)

                                it.alpha = 0.6F
                            }
                            false -> {
                                it.setIconTintRes(AIDC_ACTION_ITEM_TINT)
                                it.alpha = this.uxActionOverlay.buttonAlpha
                            }
                        }

                    }
        }

    override fun onResume() {
        super.onResume()

        this.isPaused = false

        // Customize navigation drawer

        val navHeaderView = this.uxNavView.getHeaderView(0)
        navHeaderView.uxVersion.text = "v${BuildConfig.VERSION_NAME}"
        navHeaderView.uxDeviceId.text = this.identity.shortUid.toString()

        if (this.debugSettings.enabled) {
            this.uxNavView.menu.findItem(R.id.nav_dev_remote_settings).isVisible = true
        }

        this.actionItemsProperty
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    log.info("ACTION ITEMS CAHNGED")
                    val items = mutableListOf(*it.value.toTypedArray())

                    if (this.device.manufacturer.type == Device.Manufacturer.Type.Generic) {
                        items.add(
                                0,
                                ActionItem(
                                        id = R.id.action_aidc_camera,
                                        colorRes = AIDC_ACTION_ITEM_COLOR,
                                        iconRes = R.drawable.ic_barcode,
                                        iconTintRes = AIDC_ACTION_ITEM_TINT,
                                        visible = this.aidcReader.enabled,
                                        alignEnd = false
                                )
                        )
                    } else {
                        items.add(
                                0,
                                ActionItem(
                                        id = R.id.action_aidc_keyboard,
                                        colorRes = AIDC_ACTION_ITEM_COLOR,
                                        iconRes = R.drawable.ic_keyboard,
                                        iconTintRes = AIDC_ACTION_ITEM_TINT,
                                        visible = this.aidcReader.enabled,
                                        alignEnd = false
                                )
                        )
                    }

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
                                    .findItem(R.id.nav_logout).isVisible = true

                            // Update navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.VISIBLE
                            navHeaderView.uxActiveUser.text = user.email
                            navHeaderView.uxStationId.text = "-_-"
                        }
                        else -> {
                            this.uxNavView.menu
                                    .findItem(R.id.nav_logout).isVisible = false

                            // Hide navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.GONE

                            // All activities except for Main require login
                            if (!(this is MainActivity)) {
                                this.startActivity(
                                        Intent(applicationContext, MainActivity::class.java)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                                finish()
                            }
                        }
                    }
                }

        //region AIDC
        this.aidcReader.enabledProperty
                .distinctUntilChanged()
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { enabled ->
                    log.info("AIDC reader ${if (enabled.value) "enabled" else "disabled"}")
                    val aidcActionItem = this.uxActionOverlay.items
                            .filter {
                                (it.id == R.id.action_aidc_camera || it.id == R.id.action_aidc_keyboard)
                                        &&
                                        it.visible != enabled.value
                            }
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

                    this.cameraAidcFragment?.also {
                        if (!it.isPinned)
                            this.cameraAidcFragmentVisible = false
                    }
                }
        //endregion
    }

    /**
     * Shows a screen fragment
     * @param fragment Screen fragment to show
     * @param addToBackStack If the fragment should be added to the backstack
     */
    fun showScreen(fragment: ScreenFragment<*>, addToBackStack: Boolean = true): Int {
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

    /** Most recently set scroll flags */
    private var scrollFlags: Int = 0

    override fun onScreenFragmentResume(fragment: ScreenFragment<*>) {
        this.aidcReader.enabled = fragment.aidcEnabled

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
                    this.uxHeaderAccentBar.backgroundColor = if (remoteSettings.hostIsProductive || it.value != R.color.colorAccent)
                        ContextCompat.getColor(this, it.value) else ContextCompat.getColor(this, R.color.colorDev)
                }

        fragment.flipScreenProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribe {
                    if (it.value) {
//                        this.uxContainer.animation = AnimationUtils.loadAnimation(this, R.anim.rotate180) as RotateAnimation
//                        this.uxContainer.animate()
                        this.uxContainer.rotation = -180F

                        // Hide status bar
                        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    } else {
//                        this.uxContainer.animation = AnimationUtils.loadAnimation(this, R.anim.rotate0) as RotateAnimation
//                        this.uxContainer.animate()
                        this.uxContainer.rotation = 0F

                        // Show status bar
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                }

        fragment.lockNavigationDrawerProperty
                .bindUntilEvent(fragment, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.value) {
                        true -> this.uxDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        false -> this.uxDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    }
                }

        // Setup collapsing layout, appbar & header

        this.uxCollapsingToolbarLayout.title = fragment.title

        var expandAppBar = true
        var scrollCollapseMode = fragment.scrollCollapseMode
        var scroll = (scrollCollapseMode != ScreenFragment.ScrollCollapseModeType.None)

        log.trace("HEADER HEIGHT ${this.uxHeader.layoutParams.height}")

        if (fragment.toolbarCollapsed) {
            expandAppBar = false
            scroll = true
        }

        if (fragment.toolbarHidden) {
            // Workaround for supportActionBar not adjusting content area

            // Hiding the entire appbar via expanded flag only works in conjunction
            // with collapsing toolbar scroll/snap mode
            expandAppBar = false
            scroll = true
            scrollCollapseMode = ScreenFragment.ScrollCollapseModeType.EnterAlwaysCollapsed
        }

        if (fragment.statusBarHidden) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // Apply header changes
        run {
            // TODO: don't expand when scroll position is not top on pre-existing fragment

            log.trace("HEADER IMAGE SET ${fragment.headerImage}")
            this.header.headerDrawable = if (fragment.headerImage != null)
                fragment.headerImage
            else
                this.header.defaultDrawable
        }

        // Apply collapsing toolbar settings
        run {
            // EXIT_UNTIL_COLLAPSED should always be the default, so title and appbar expansion works properly
            val collapsingScrollFlag = when (scrollCollapseMode) {
                ScreenFragment.ScrollCollapseModeType.ExitUntilCollapsed -> AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                ScreenFragment.ScrollCollapseModeType.EnterAlways -> AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                ScreenFragment.ScrollCollapseModeType.EnterAlwaysCollapsed -> AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED

                ScreenFragment.ScrollCollapseModeType.None -> AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            }

            val scrollFlag = when (scroll) {
                false -> 0
                else -> AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            }

            val scrollSnapFlag = when (fragment.scrollSnap) {
                true -> AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                false -> 0
            }

            log.trace("APPBAR EXPAND ${expandAppBar}")
            this.uxAppBarLayout.setExpanded(expandAppBar, true)

            val layoutParams = this.uxCollapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams

            this.scrollFlags = scrollFlag or collapsingScrollFlag or scrollSnapFlag
            log.trace("SCROLL FLAGS ${layoutParams.scrollFlags} ${scrollFlags}")

            this.uxCollapsingToolbarLayout.postDelayed({
                if (layoutParams.scrollFlags != this.scrollFlags) {
                    layoutParams.scrollFlags = this.scrollFlags
                    this.uxAppBarLayout.setExpanded(expandAppBar)
                }
            }, 300)
        }

        // Apply requested orientation
        this.requestedOrientation = when {
            debugSettings.userScreenRotation -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // Enforce orientation on every fragment resume
            else -> fragment.orientation
        }

    }

    override fun onScreenFragmentPause(fragment: ScreenFragment<*>) {
        this.cameraAidcFragmentVisible = false
    }

    /**
     * On foreground handler (hooked into ApplicationStateMonitor)
     */
    private fun onForeground() {
        // Check developer settings
        if (!debugSettings.allowDeveloperOptions && Settings.Secure.getString(this.contentResolver, Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED) == "1") {
            MaterialDialog.Builder(this)
                    .title(getString(R.string.dialog_title_developer_enabled))
                    .content(getString(R.string.dialog_text_developer_enabled))
                    .positiveText("Settings")
                    .negativeText("Abort")
                    .onPositive { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        this.startActivityForResult(intent, 0)
                    }
                    .onNegative { _, _ -> this.finishAffinity() }
                    .cancelable(false)
                    .show()
        }

        val locationManager = this.locationManager
        if (sequenceOf(
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER).also {
                    log.trace("LOCATION PROVIDER GPS $it")
                },
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER).also {
                    log.trace("LOCATION PROVIDER NETWORK $it")
                }
        ).all { false }) {
            MaterialDialog.Builder(this)
                    .title(getString(R.string.dialog_title_gps_disabled))
                    .content(getString(R.string.dialog_text_gps_disabled))
                    .positiveText("Settings")
                    .negativeText("Abort")
                    .onPositive { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        this.startActivityForResult(intent, 0)
                    }
                    .onNegative { _, _ -> this.finishAffinity() }
                    .cancelable(false)
                    .show()
        }
    }
}
