package org.deku.leoz.mobile.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.screen.MainScreen
import org.slf4j.LoggerFactory
import android.content.SharedPreferences
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import com.afollestad.materialdialogs.MaterialDialog
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.device.Tones


class MainActivity
    :
        Activity(),
        LoginFragment.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val login: Login by Kodein.global.lazy.instance()

    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            this.supportActionBar?.title = getText(R.string.login)

            this.showScreen(
                    MainScreen(),
                    addToBackStack = false
            )

            if (this.login.authenticatedUser != null) {
                this.onLoginSuccessful()
            }
        }
    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.backStackEntryCount == 0) {
            showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        this.loginPendingDialog.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showExitDialog() {
        MaterialDialog.Builder(this)
                .title(R.string.exit_application)
                .content(R.string.exit_application_prompt)
                .positiveText(android.R.string.yes)
                .onPositive { dialog, which -> this.app.terminate() }
                .negativeText(android.R.string.no)
                .build().show()
    }

    val loginPendingDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.login_pending)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build()
    }

    //region LoginFragment listener
    override fun onLoginSuccessful() {
        this.startActivity(
                Intent(applicationContext, DeliveryActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        this.finish()
    }

    override fun onLoginPending() {
        this.loginPendingDialog.show()
    }

    override fun onLoginFailed() {
        this.loginPendingDialog.dismiss()

        tones.errorBeep()

        this.snackbarBuilder
                .message(R.string.authentication_failed)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show()
    }
    //endregion
}