package org.deku.leoz.mobile.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tinsuke.icekick.extension.serialState
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.deku.leoz.mobile.service.UpdateService
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.config.LogConfiguration
import org.deku.leoz.mobile.model.service.create
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.service.LocationService
import org.deku.leoz.mobile.ui.BaseActivity
import org.deku.leoz.mobile.ui.extension.showErrorAlert
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import sx.Stopwatch
import sx.android.Device
import sx.android.aidc.AidcReader
import sx.mq.mqtt.channel
import java.util.concurrent.TimeUnit


/**
 * Responsible for routing intents to activities and displaying splash screen
 * Created by masc on 27.03.14.
 */
class StartupActivity : BaseActivity() {
    val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val EXTRA_ACTIVITY = "ACTIVITY"
    }

    /**
     * Start main activitiy
     */
    private fun startMainActivity(withAnimation: Boolean) {
        val activityName = this.intent.getStringExtra(EXTRA_ACTIVITY)
                ?: MainActivity::class.java.canonicalName

        log.trace("STARTUP ACTIVITY ${activityName}")
        this.startActivity(
                Intent(this, Class.forName(activityName)))

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

        // Load log configuration first
        Kodein.global.instance<LogConfiguration>()
        Kodein.global.instance<Application>()

        log.info("${this.app.name} v${this.app.version}")
        log.trace("Intent action ${this.intent.action}")

        if (!this.app.isInitialized) {
            val ovDatabase = Observable.fromCallable {
            }

            // Acquire permissions
            val ovPermissions = RxPermissions(this)
                    .request(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
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
            val ovAidcReader = Kodein.global.instance<Observable<out AidcReader>>()
                    .timeout(5, TimeUnit.SECONDS)
                    .onErrorReturn {
                        throw IllegalStateException("AidcReader initialization timed out", it)
                    }

            // Merge and subscribe
            Observable.mergeArray(
                    ovPermissions.cast(Any::class.java),
                    ovAidcReader.cast(Any::class.java)
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onComplete = {
                                try {
                                    // Any exceptions thrown in this block will result in a error message
                                    // dialog to be shown including the exception message

                                    // Log device info/serial
                                    val device: Device = Kodein.global.instance()
                                    log.info(device.toString())

                                    val identity: Identity = Kodein.global.instance()
                                    log.info(identity.toString())

                                    val locale = resources.configuration.locale
                                    log.info("Current locale [${locale.displayName}] country [${locale.displayCountry}] language [${locale.displayLanguage}]")

                                    // Prepare database
                                    val database: Database = Kodein.global.instance()

                                    try {
                                        Stopwatch.createStarted(this, "Preparing database [${database.dataSource.databaseName}]", { sw, log ->
                                            // Simply getting a writable database reference will perform (requery) migration
                                            database.dataSource.writableDatabase
                                        })
                                    } catch (e: Throwable) {
                                        // Build error message
                                        var text = "${this.getText(org.deku.leoz.mobile.R.string.error_database_inconsistent)}"
                                        text += if (e.message != null) " (${e.message})" else ""
                                        text += ". ${this.getText(org.deku.leoz.mobile.R.string.prompt_reinstall)}"

                                        throw RuntimeException(text, e)
                                    }

                                    // Start update service
                                    Kodein.global.instance<UpdateService>()

                                    // Late initialization of singletons which require eg. permissions
                                    Kodein.global.instance<IMqttAsyncClient>()
                                    Kodein.global.instance<LogMqAppender>().also {
                                        it.dispatcher.start()
                                    }

                                    // Initialize location service
                                    run {
                                        val locationSettings = Kodein.global.instance<LocationSettings>()

                                        if (locationSettings.enabled) {
                                            this.startService(
                                                    Intent(applicationContext, LocationService::class.java))
                                        }
                                    }

                                    // Send authorization message
                                    run {
                                        val mqEndpoints = Kodein.global.instance<MqttEndpoints>()
                                        mqEndpoints.central.main.channel().send(
                                                AuthorizationService.NodeRequest(
                                                        key = identity.uid.value,
                                                        name = BundleType.LeozMobile.value,
                                                        systemInfo = SystemInformation.create(this.app, device).let {
                                                            ObjectMapper().writeValueAsString(it)
                                                        }
                                                )
                                        )
                                    }

                                    // Start main activity
                                    val handler = Handler()
                                    handler.postDelayed({
                                        this@StartupActivity.startMainActivity(withAnimation = true)
                                    }, 300)
                                    this.app.isInitialized = true
                                } catch (e: Throwable) {
                                    log.error(e.message, e)

                                    this.showErrorAlert(text = e.message ?: e.javaClass.simpleName, onPositiveButton = {
                                        this.app.terminate()
                                    })
                                }
                            },
                            onError = { e ->
                                log.error(e.message, e)

                                this@StartupActivity.finishAffinity()
                            })
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
}
