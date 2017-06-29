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
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.jetbrains.anko.contentView
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.main_content.*
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

    override fun onDestroy() {
        this.loginPendingDialog.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.exit_application)
                .setMessage(R.string.exit_application_prompt)
                .setPositiveButton(android.R.string.yes, { dialog, which -> System.exit(0) })
                .setNegativeButton(android.R.string.no, null)

        builder.create().show()
    }

    val loginPendingDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.login_pending)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build()
    }

    //region LoginFragment listener
    override fun onLoginSuccessful() {
        this.startActivity(
                Intent(applicationContext, DeliveryActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onLoginPending() {
        this.loginPendingDialog.show()
    }

    override fun onLoginFailed() {
        this.loginPendingDialog.dismiss()

        tone.errorBeep()

        Snackbar.make(this.snackbarParentView,
                "Login failed",
                Snackbar.LENGTH_SHORT
        )
                .show()
    }
    //endregion
}