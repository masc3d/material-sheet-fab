package org.deku.leoz.mobile.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.prototype.activities.Proto_MainActivity
import org.deku.leoz.mobile.ui.AlertButton
import org.deku.leoz.mobile.ui.showAlert
import org.deku.leoz.mobile.ui.showErrorAlert
import org.deku.leoz.mobile.update.UpdateService
import org.slf4j.LoggerFactory
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.subscribeWith

class MainActivity : Activity() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup fab button
        this.fab.setOnClickListener { view ->
            Snackbar.make(view, "Call Supervisor for assistance?", Snackbar.LENGTH_LONG).setAction("Action", {
                this@MainActivity.showAlert(
                        title = "Call assistance?",
                        text = "Are you sure you want to call a supervisor?",
                        positiveButton = AlertButton(text = R.string.yes, handler = {
                            val intent = Intent(this, Proto_MainActivity::class.java)
                            startActivity(intent)
                        }),
                        negativeButton = AlertButton(text = R.string.cancel))
            }).show()
        }

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
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
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

        // UI
        this.uxVersion.text = "v${BuildConfig.VERSION_NAME}"
    }
}
