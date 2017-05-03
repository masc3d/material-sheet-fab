package org.deku.leoz.mobile.ui.activity

import android.app.AlertDialog
import android.content.Context
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
import android.view.LayoutInflater




class MainActivity : Activity() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val PRIVATE_PREF = "leoz.app"
    private val VERSION_KEY = "version_number"

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

        //Initiate ChangelogDialog  (Whats New)
        val sharedPref = getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE)
        var currentVersionNumber = 0
        val savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0)

        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            currentVersionNumber = pi.versionCode
        } catch (e: Exception) {

        }

        if (currentVersionNumber > savedVersionNumber) {
            showWhatsNewDialog()

            val editor = sharedPref.edit()

            editor.putInt(VERSION_KEY, currentVersionNumber)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showWhatsNewDialog() {
        val inflater = LayoutInflater.from(this)

        val view = inflater.inflate(R.layout.dialog_whatsnew, null)

        val builder = AlertDialog.Builder(this)

        builder.setView(view)
                .setPositiveButton(getString(R.string.dismiss), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        builder.create().show()
    }
}