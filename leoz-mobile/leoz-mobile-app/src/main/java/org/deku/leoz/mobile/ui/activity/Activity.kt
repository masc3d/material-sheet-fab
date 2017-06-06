package org.deku.leoz.mobile.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.main_nav_header.view.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.fragment.AidcCameraFragment
import org.slf4j.LoggerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.prototype.activities.ProtoMainActivity
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.fragment.util.withTransaction
import sx.android.widget.setColors

/**
 * Leoz activity base class
 * Created by n3 on 23/02/2017.
 */
open class Activity : RxAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()
    private val tone: Tone by Kodein.global.lazy.instance()
    private val updateService: UpdateService by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(org.deku.leoz.mobile.R.layout.main)

        this.nav_view.setNavigationItemSelectedListener(this)

        //region Action bar
        setSupportActionBar(this.toolbar)

        val toggle = ActionBarDrawerToggle(
                this,
                this.drawer_layout,
                this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        this.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //endregion

        //region Camera control
        this.cameraAidcFragmentVisible = false

        this.uxAidcCameraFab.setOnClickListener { view ->
            this.cameraAidcFragmentVisible = !this.cameraAidcFragmentVisible
        }
        //endregion

        //region Update service UI feedback
        val updateService: UpdateService = Kodein.global.instance()

        updateService.availableUpdateEvent
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { event ->
                            val sb = Snackbar.make(
                                    this@Activity.uxContainer,
                                    this@Activity.getString(org.deku.leoz.mobile.R.string.version_available, event.version),
                                    Snackbar.LENGTH_INDEFINITE)
                            sb.setAction(org.deku.leoz.mobile.R.string.update, {
                                sb.dismiss()
                                event.apk.install(this@Activity)
                            })
                            sb.show()

                            val navItem: TextView? = findViewById(R.id.nav_check_updates) as TextView
                            navItem?.gravity = Gravity.CENTER_VERTICAL
                            navItem?.setTypeface(null, Typeface.BOLD)
                            navItem?.setTextColor(resources.getColor(R.color.colorAccent))
                            navItem?.text = "1+"
                        })
        //endregion
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
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
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
            when (value) {
                true -> {
                    this.uxAidcCameraFab.setColors(backgroundTint = R.color.colorDarkGrey, iconTint = R.color.colorAccent)
                    this.uxAidcCameraFab.alpha = 0.6F
                }
                false -> {
                    this.uxAidcCameraFab.setColors(backgroundTint = R.color.colorAccent, iconTint = android.R.color.black)
                    this.uxAidcCameraFab.alpha = 0.85F
                }
            }

            if (value == this.cameraAidcFragmentVisible)
                return

            val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
            if (!value) {
                this.supportFragmentManager.withTransaction {
                    it.remove(fragment)
                }
            } else {
                this.supportFragmentManager.withTransaction {
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

                            // Anywhere else but in main, actively logout
                            if (this.javaClass != MainActivity::class.java) {
                                this.startActivity(
                                        Intent(applicationContext, MainActivity::class.java)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                        Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
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