package org.deku.leoz.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.fragment.AidcCameraFragment
import org.slf4j.LoggerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.main_nav_header.*
import kotlinx.android.synthetic.main.main_nav_header.view.*
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.prototype.activities.ProtoMainActivity
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.deku.leoz.mobile.ui.activity.MainActivity
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.view.ActionOverlayView
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.fragment.util.withTransaction
import sx.android.view.setColors
import sx.rx.ObservableRxProperty

/**
 * Leoz activity base class
 * Created by n3 on 23/02/2017.
 */
open class Activity : RxAppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ActionOverlayView.Listener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()
    private val tone: Tone by Kodein.global.lazy.instance()
    private val updateService: UpdateService by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    /** Action items */
    val actionItemsProperty = ObservableRxProperty<List<ActionItem>>(listOf())
    var actionItems by actionItemsProperty

    private val actionEventSubject = PublishSubject.create<Int>()
    val actionEvent = this.actionEventSubject.hide()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.main)

        this.nav_view.setNavigationItemSelectedListener(this)

        //region Action bar
        this.setSupportActionBar(this.toolbar)

        val toggle = ActionBarDrawerToggle(
                this,
                this.drawer_layout,
                this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        this.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //endregion

        this.uxActionOverlay.fabStyle = R.style.AppTheme_Fab
        this.uxActionOverlay.listener = this

        this.cameraAidcFragmentVisible = false
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

        // Show logout only if there's a user actually logged in
        menu
                .findItem(R.id.action_logout)
                .setVisible(this.login.authenticatedUser != null)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                return false
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

            R.id.nav_dev_login -> {
                this.nav_view.postDelayed({
                    login.authenticate(
                            email = Login.DEV_EMAIL,
                            password = Login.DEV_PASSWORD

                    )
                            .subscribeBy(
                                    onNext = {
                                        this.startActivity(
                                                Intent(applicationContext, DeliveryActivity::class.java)
                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                                Intent.FLAG_ACTIVITY_NEW_TASK))
                                        finish()

                                    },
                                    onError = {
                                        tone.errorBeep()
                                    }
                            )
                }, 20)
            }

            R.id.nav_logout -> {
                this.nav_view.postDelayed({
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
                        aidcFab.setColors(backgroundTint = R.color.colorAccent, iconTint = android.R.color.black)
                        aidcFab.alpha = 0.85F
                    }
                }
            }

            if (value == this.cameraAidcFragmentVisible)
                return

            if (!value) {
                val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
                if (fragment != null) {
                    // Fragment removal cannot be animated, thus doing it manually
                    ViewCompat.animate(fragment.view)
                            .translationY(fragment.view!!.height.toFloat())
                            .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                            .withEndAction {
                                this@Activity.supportFragmentManager.withTransaction {
                                    it.remove(fragment)
                                }
                            }
                            .start()
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

        // Customize navigation drawer

        val navHeaderView = this.drawer_layout.nav_view.getHeaderView(0)
        navHeaderView.uxVersion.text = "v${BuildConfig.VERSION_NAME}"

        if (this.debugSettings.enabled) {
            this.nav_view.menu.findItem(R.id.nav_dev_login).setVisible(true)
            this.nav_view.menu.findItem(R.id.nav_dev_prototype).setVisible(true)
        }

        this.actionItemsProperty
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    val items = mutableListOf(*it.value.toTypedArray())
                    items.add(
                            ActionItem(
                                    id = R.id.action_aidc_camera,
                                    colorRes = R.color.colorAccent,
                                    iconRes = R.drawable.ic_barcode
                            )
                    )
                    this.uxActionOverlay.items = items
                }

        this.updateService.availableUpdateEvent
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { event ->
                            val sb = Snackbar.make(
                                    this.uxContainer,
                                    this@Activity.getString(R.string.version_available, event.version),
                                    Snackbar.LENGTH_INDEFINITE)
                            sb.setAction(R.string.update, {
                                event.apk.install(this@Activity)
                            })
                            sb.show()
                        })


        // Authentication changes

        this.login.authenticatedUserProperty
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    this@Activity.invalidateOptionsMenu()

                    val user = it.value
                    when {
                        user != null -> {
                            this.nav_view.menu
                                    .findItem(R.id.nav_logout)
                                    .setVisible(true)

                            this.nav_view.menu
                                    .findItem(R.id.nav_dev_prototype)
                                    .setVisible(this.debugSettings.enabled)

                            this.nav_view.menu
                                    .findItem(R.id.nav_dev_login)
                                    .setVisible(false)

                            // Update navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.VISIBLE
                            navHeaderView.uxActiveUser.text = user.email
                            navHeaderView.uxStationID.text = "-_-"
                        }
                        else -> {
                            this.nav_view.menu
                                    .findItem(R.id.nav_logout)
                                    .setVisible(false)

                            this.nav_view.menu
                                    .findItem(R.id.nav_dev_prototype)
                                    .setVisible(false)

                            this.nav_view.menu
                                    .findItem(R.id.nav_dev_login)
                                    .setVisible(this.debugSettings.enabled)

                            // Hide navigation header
                            navHeaderView.uxUserAreaLayout.visibility = View.GONE
                        }
                    }
                }

        this.aidcReader.bindActivity(this)

        this.cameraReader.readEvent
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.tone.beep()
                    this.cameraAidcFragmentVisible = false
                }
    }
}