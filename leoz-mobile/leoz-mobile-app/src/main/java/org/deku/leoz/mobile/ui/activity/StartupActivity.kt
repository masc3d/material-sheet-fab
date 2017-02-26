package org.deku.leoz.mobile.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.tinsuke.icekick.state
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.update.UpdateService
import org.slf4j.LoggerFactory
import sx.android.Device
import android.support.v4.app.ActivityCompat.requestPermissions
import com.tbruyelle.rxpermissions.RxPermissions
import org.deku.leoz.mobile.model.Database
import rx.lang.kotlin.subscribeWith


/**
 * Responsible for routing intents to activities and displaying splash screen
 * Created by masc on 27.03.14.
 */
class StartupActivity : RxAppCompatActivity() {
    val log = LoggerFactory.getLogger(this.javaClass)

    private var started: Boolean by state(false)

    /**
     * Start main activitiy
     */
    private fun startMainActivity(withAnimation: Boolean) {
        val i = Intent(this@StartupActivity, MainActivity::class.java)
        this.startActivity(i)
        if (withAnimation)
            this.overridePendingTransition(org.deku.leoz.mobile.R.anim.main_fadein, org.deku.leoz.mobile.R.anim.splash_fadeout)

        this.finish()
    }

    /**
     * Activity creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state
        this.app.unfreezeInstanceState(this)

        // Load some more heavy-weight instances on startup/splash
        val app: Application = Kodein.global.instance()
        val db: Database = Kodein.global.instance()
        val update: UpdateService = Kodein.global.instance()

        log.info("${this.app.name} v${this.app.version}")
        log.trace("Intent action ${this.intent.action}")

        if (!this.started) {
            // TODO: this implementation could/should be fully reactive

            // Start database migration (async)
            val migrateAwaitable = db.migrate()

            // Ensure permissions are granted
            val rxPermissions = RxPermissions(this)
            rxPermissions
                    .request(Manifest.permission.READ_PHONE_STATE)
                    .subscribe { granted ->
                        if (granted) {
                            // Log serials
                            val device: Device = Kodein.global.instance()
                            log.info(device.toString())

                            // Start main activity with delay to ensure visibility of transition
                            val handler = Handler()
                            handler.postDelayed({
                                this.startMainActivity(withAnimation = true)

                                // Wait for migration to finish
                                try {
                                    migrateAwaitable.await()
                                } catch(e: Throwable) {
                                    // Ignore this exception here, as StartupActivity is about to finish.
                                    // Migration result will be evaluated in MainActivity
                                }
                            }, 300)

                            this.started = true
                        } else {
                            // Permissions not granted, terminating
                            this@StartupActivity.app.terminate()
                        }
                    }
        } else {
            this.startMainActivity(withAnimation = false)
        }
    }

    /**
     * Activity pause
     */
    override fun onPause() {
        super.onPause()

        // Save state
        this.app.freezeInstanceState(this)
    }

    /**
     * Activity destroy
     */
    override fun onDestroy() {
        super.onDestroy()
        log.trace("ONDESTROY")
    }
}
