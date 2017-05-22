package org.deku.leoz.mobile.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import kotlinx.android.synthetic.main.main_app_bar.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.model.Database
import org.deku.leoz.mobile.prototype.activities.ProtoMainActivity
import org.deku.leoz.mobile.ui.AlertButton
import org.deku.leoz.mobile.ui.fragment.MainFragment
import org.deku.leoz.mobile.ui.showAlert
import org.deku.leoz.mobile.ui.showErrorAlert
import org.slf4j.LoggerFactory
import sx.android.fragment.util.withTransaction
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.LayoutInflater
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.main_nav_header.*
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.model.User
import org.deku.leoz.mobile.ui.fragment.MenuFragment
import org.deku.leoz.mobile.SharedPreference


class MainActivity : Activity() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val login: Login by Kodein.global.lazy.instance()
    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()

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

        this.supportActionBar?.title = getText(R.string.login)

        this.supportFragmentManager.withTransaction {
            it.replace(R.id.uxContainer, MainFragment())
        }

        // Setup fab button
        this.uxHelpFab.setOnClickListener { view ->
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
    }

    override fun onBackPressed() {
        if (login.authenticatedUser?.hash?.isBlank() ?: true) {
            showExitDialog()
            return
        }
        super.onBackPressed()
    }

    /**
     * Determine if changelog should be displayed automatically e.g. after an APP update.
     * Display the dialog only after the user has been logged in
     */
    fun queryChangelogDisplay() {
        var currentVersionNumber = 0
        val savedVersionNumber = sharedPreferences.getInt(SharedPreference.CHANGELOG_VERSION.key, 0)

        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            currentVersionNumber = pi.versionCode
        } catch (e: Exception) {
            log.error("${e.message}\r\n${e.stackTrace}")
        }

        log.debug("Checking for changelog dialog. Current version [$currentVersionNumber] Recently saved version [$savedVersionNumber]")

        if (currentVersionNumber > savedVersionNumber) {
            showChangelogDialog()

            val editor = sharedPreferences.edit()

            editor.putInt(SharedPreference.CHANGELOG_VERSION.key, currentVersionNumber)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()

        login.authenticatedUserProperty
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val user: User? = it.value
                    if (user != null && user.hash.isNotBlank()) {
                        uxActiveUser?.text = user.name
                        this.supportActionBar?.title = "Menue"
                        this.supportFragmentManager.withTransaction {
                            it.replace(R.id.uxContainer, MenuFragment())
                        }
                        queryChangelogDisplay()
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
}