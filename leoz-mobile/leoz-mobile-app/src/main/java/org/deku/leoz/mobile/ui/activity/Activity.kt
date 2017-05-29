package org.deku.leoz.mobile.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
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
import org.deku.leoz.mobile.model.Login
import org.w3c.dom.Text
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.trace("ONCREATE")

        this.setContentView(org.deku.leoz.mobile.R.layout.main)

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

        // Setup navigation drawer
        val navHeaderView = this.drawer_layout.nav_view.getHeaderView(0)
        navHeaderView.uxVersion.text = "v${BuildConfig.VERSION_NAME}"

        this.nav_view.setNavigationItemSelectedListener(this)

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

                            val navItem: TextView? = findViewById(R.id.nav_trigger_updateservice) as TextView
                            navItem?.gravity = Gravity.CENTER_VERTICAL
                            navItem?.setTypeface(null, Typeface.BOLD)
                            navItem?.setTextColor(resources.getColor(R.color.colorAccent))
                            navItem?.text = "1+"
                        })

        //endregion

        // Invalidate options menu on specific model changes

        this.login.authenticatedUserProperty
                .subscribe {
                    this@Activity.invalidateOptionsMenu()
                }
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
                startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        log.debug("ONNAVIGATIONITEMSELECTED [$id]")

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_trigger_updateservice) {
            updateService.trigger()
        } else if (id == R.id.nav_send) {

        }

        this.drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
                    this.uxHelpFab.alpha = 0.6F
                }
                false -> {
                    this.uxAidcCameraFab.setColors(backgroundTint = R.color.colorAccent, iconTint = android.R.color.black)
                    this.uxAidcCameraFab.alpha = 0.85F
                    this.uxHelpFab.alpha = 0.85F
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

    override fun onStart() {
        super.onStart()

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