package org.deku.leoz.mobile.ui.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
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
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.subscribeWith
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
        onCameraEnabled(false)

        this.uxAidcCameraFab.setOnClickListener { view ->
            this.cameraAidcFragmentVisible = !this.cameraAidcFragmentVisible
        }
        //endregion

        //region Update service UI feedback
        val updateService: UpdateService = Kodein.global.instance()

        updateService.availableUpdateEvent
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith {
                    onNext { event ->
                        val sb = Snackbar.make(
                                this@Activity.uxContainer,
                                this@Activity.getString(org.deku.leoz.mobile.R.string.version_available, event.version),
                                Snackbar.LENGTH_INDEFINITE)
                        sb.setAction(org.deku.leoz.mobile.R.string.update, {
                            sb.dismiss()
                            event.apk.install(this@Activity)
                        })
                        sb.show()
                    }
                }

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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        this.drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onCameraEnabled(enabled: Boolean) {
        when (enabled) {
            true -> {
                this.uxAidcCameraFab.setColors(backgroundTint = R.color.colorDarkGrey, iconTint = R.color.colorAccent)
                this.uxAidcCameraFab.alpha = 0.5F
                this.uxHelpFab.alpha = 0.5F
            }
            false -> {
                this.uxAidcCameraFab.setColors(backgroundTint = R.color.colorAccent, iconTint = android.R.color.black)
                this.uxAidcCameraFab.alpha = 1.0F
                this.uxHelpFab.alpha = 1.0F
            }
        }
    }

    private var cameraAidcFragmentVisible: Boolean
        get() {
            val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
            return fragment != null
        }
        set(value) {
            if (value == this.cameraAidcFragmentVisible)
                return

            val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
            if (!value) {
                onCameraEnabled(false)

                this.supportFragmentManager.withTransaction {
                    it.remove(fragment)
                }
            } else {
                onCameraEnabled(true)

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