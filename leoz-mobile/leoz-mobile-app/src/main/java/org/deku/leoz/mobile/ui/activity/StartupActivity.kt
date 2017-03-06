package org.deku.leoz.mobile.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.genericInstance
import com.tbruyelle.rxpermissions.RxPermissions
import com.tinsuke.icekick.extension.serialState
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import org.deku.leoz.mobile.Application
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.freezeInstanceState
import org.deku.leoz.mobile.model.Database
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.unfreezeInstanceState
import org.slf4j.LoggerFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.onErrorReturnNull
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers
import sx.android.Device
import sx.android.aidc.AidcReader
import java.util.concurrent.TimeUnit


/**
 * Responsible for routing intents to activities and displaying splash screen
 * Created by masc on 27.03.14.
 */
class StartupActivity : RxAppCompatActivity() {
    val log = LoggerFactory.getLogger(this.javaClass)

    private var started: Boolean by serialState(false)

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
            val ovMigrate = db.migrate()
                    // Ignore this exception here, as StartupActivity is about to finish.
                    // Migration result will be evaluated in MainActivity
                    .onErrorReturnNull()
                    .ignoreElements()

            // Acquire permissions
            val ovPermissions = RxPermissions(this)
                    .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                    .switchMap<Boolean> {
                        when (it) {
                            true -> {
                                log.info("Required permissions granted")
                                Observable.empty()
                            }
                            false -> {
                                // As .terminate will kill the process, this exception won't throw
                                throw IllegalStateException("Permissions not granted")
                            }
                        }
                    }

            // Acquire AidcReader
            val ovAidcReader = Kodein.global.genericInstance<Observable<out AidcReader>>()
                    .timeout(5, TimeUnit.SECONDS)
                    .onErrorReturn {
                        throw IllegalStateException("AidcReader initialization timed out", it)
                    }

            // Merge and subscribe
            Observable.merge(arrayOf(
                    ovMigrate.cast(Any::class.java),
                    ovPermissions.cast(Any::class.java),
                    ovAidcReader.cast(Any::class.java)
            ))
                    .onBackpressureBuffer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith {
                        onCompleted {
                            // Log device info/serial
                            val device: Device = Kodein.global.instance()
                            log.info(device.toString())

                            val handler = Handler()
                            handler.postDelayed({
                                this@StartupActivity.startMainActivity(withAnimation = true)
                            }, 300)
                            this@StartupActivity.started = true
                        }
                        onError { e ->
                            log.error(e.message, e)
                            this@StartupActivity.finishAffinity()
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
