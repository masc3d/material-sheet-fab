package org.deku.leoz.mobile.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.ui.AlertButton
import org.deku.leoz.mobile.ui.screen.MainScreen
import org.deku.leoz.mobile.ui.showAlert
import org.deku.leoz.mobile.ui.showErrorAlert
import org.slf4j.LoggerFactory
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.LayoutInflater
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.SharedPreference
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.dialog.ChangelogDialog
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.jetbrains.anko.contentView
import com.afollestad.materialdialogs.MaterialDialog
import org.deku.leoz.mobile.device.Tone


class MainActivity
    :
        Activity(),
        LoginFragment.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val login: Login by Kodein.global.lazy.instance()

    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()
    private val tone: Tone by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check (asynchronous) database migration result
        val database: Database.Migration = Kodein.global.instance()

        val migrationResult = database.result
        if (migrationResult != null) {
            // Build error message
            var text = "${this.getText(org.deku.leoz.mobile.R.string.error_database_inconsistent)}"
            text += if (migrationResult.message != null) " (${migrationResult.message})" else ""
            text += ". ${this.getText(org.deku.leoz.mobile.R.string.prompt_reinstall)}"

            this.showErrorAlert(text = text, onPositiveButton = {
                this.app.terminate()
            })
        }

        this.supportActionBar?.title = getText(R.string.login)

        this.showScreen(
                MainScreen(),
                addToBackStack = false
        )
    }

    override fun onBackPressed() {
        showExitDialog()
        return
    }

    override fun onResume() {
        super.onResume()

        this.actionEvent
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_help -> {
                            Snackbar.make(this@MainActivity.contentView!!, "Call Supervisor for assistance?", Snackbar.LENGTH_LONG).setAction("Action", {
                                this@MainActivity.showAlert(
                                        title = "Call assistance?",
                                        text = "Are you sure you want to call a supervisor?",
                                        positiveButton = AlertButton(text = android.R.string.yes, handler = {
                                        }),
                                        negativeButton = AlertButton(text = android.R.string.cancel))
                            }).show()
                        }
                    }
                }
    }

    private fun showChangelogDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_whatsnew, null)
        val builder = AlertDialog.Builder(this)

        builder.setView(view)
                .setPositiveButton(getString(R.string.dismiss), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        builder.create().show()
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Exit application?")
                .setMessage("Do you really want to exit the application?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which -> System.exit(0) })
                .setNegativeButton("No", null)

        builder.create().show()
    }

    override fun onLoginSuccessful() {
        this.startActivity(
                Intent(applicationContext, DeliveryActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onLoginPending() {
        MaterialDialog.Builder(this)
                .title(R.string.login_pending)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show()
    }

    override fun onLoginFailed() {
        tone.errorBeep()
        Snackbar.make(this.contentView!!, "Login failed", Snackbar.LENGTH_SHORT)
                .show()
    }
}