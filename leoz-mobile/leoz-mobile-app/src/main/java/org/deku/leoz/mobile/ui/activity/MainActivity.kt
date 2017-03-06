package org.deku.leoz.mobile.ui.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.genericInstance
import com.github.salomonbrys.kodein.lazy
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.model.Database
import org.deku.leoz.mobile.ui.fragment.AidcCameraFragment
import org.deku.leoz.mobile.prototype.activities.ProtoMainActivity
import org.deku.leoz.mobile.ui.AlertButton
import org.deku.leoz.mobile.ui.fragment.MainFragment
import org.deku.leoz.mobile.ui.showAlert
import org.deku.leoz.mobile.ui.showErrorAlert
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.prototype.AidcCameraView
import org.slf4j.LoggerFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.subscribeWith
import sx.android.aidc.AidcReader
import sx.android.aidc.Ean13Decoder
import sx.android.aidc.Ean8Decoder
import sx.android.fragment.util.withTransaction
import sx.android.rx.bindToLifecycle
import sx.android.widget.setBackgroundTint
import sx.android.widget.setIconTint

class MainActivity : Activity() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check (asynchronous) database migration result
        val database: Database = Kodein.global.instance()

        val migrationResult = database.migrationResult
        if (migrationResult != null) {
            // Build error message
            var text = "${this.getText(org.deku.leoz.mobile.R.string.error_database_inconsistent)}"
            text += if (migrationResult.message != null) " (${migrationResult.message})" else ""
            text += ". ${this.getText(org.deku.leoz.mobile.R.string.prompt_reinstall)}"

            this.showErrorAlert(text = text, onPositiveButton = {
                this.app.terminate()
            })
        }

        // Update service
        val updateService: UpdateService = Kodein.global.instance()

        updateService.availableUpdateEvent
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith {
                    onNext { event ->
                        val sb = Snackbar.make(
                                this@MainActivity.fab,
                                this@MainActivity.getString(org.deku.leoz.mobile.R.string.version_available, event.version),
                                Snackbar.LENGTH_INDEFINITE)
                        sb.setAction(org.deku.leoz.mobile.R.string.update, {
                            sb.dismiss()
                            event.apk.install(this@MainActivity)
                        })
                        sb.show()
                    }
                }

        this.supportActionBar?.title = getText(R.string.login)

        this.supportFragmentManager.withTransaction {
            it.replace(R.id.uxContainer, MainFragment())
        }

        // Setup fab button
        this.fab.setOnClickListener { view ->
            Snackbar.make(view, "Call Supervisor for assistance?", Snackbar.LENGTH_LONG).setAction("Action", {
                this@MainActivity.showAlert(
                        title = "Call assistance?",
                        text = "Are you sure you want to call a supervisor?",
                        positiveButton = AlertButton(text = android.R.string.yes, handler = {
                            val intent = Intent(this, ProtoMainActivity::class.java)
                            startActivity(intent)
                        }),
                        negativeButton = AlertButton(text = android.R.string.cancel))
            }).show()
        }

        this.fab_aidc_camera.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent))

        this.fab_aidc_camera.setOnClickListener { view ->
            val fragment = this.supportFragmentManager.findFragmentByTag(AidcCameraFragment::class.java.canonicalName)
            if (fragment != null) {
                this.fab_aidc_camera.setBackgroundTint(R.color.colorAccent)
                this.fab_aidc_camera.setIconTint(android.R.color.black)

                this.supportFragmentManager.withTransaction {
                    it.remove(fragment)
                }
            } else {
                this.fab_aidc_camera.setBackgroundTint(R.color.colorDarkGrey)
                this.fab_aidc_camera.setIconTint(R.color.colorAccent)

                this.supportFragmentManager.withTransaction {
                    it.replace(R.id.uxScannerContainer, AidcCameraFragment(), AidcCameraFragment::class.java.canonicalName)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}