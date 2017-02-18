package org.deku.leoz.mobile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.deku.leoz.mobile.prototype.activities.Proto_MainActivity
import org.slf4j.LoggerFactory

class MainActivity : RxAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val log = LoggerFactory.getLogger(this.javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.trace("ONCREATE")

        this.setContentView(R.layout.activity_main)
        setSupportActionBar(this.toolbar)

        this.fab.setOnClickListener { view -> Snackbar.make(view, "Call Supervisor for assistance?", Snackbar.LENGTH_LONG).setAction("Action", {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val intent : Intent
                    intent = Intent(this, Proto_MainActivity::class.java)
                    startActivity(intent)
                }
            )
            alertDialog.setNegativeButton("No", null)
            alertDialog.setTitle("Call assistance?")
            alertDialog.setMessage("Are you sure you want to call a supervisor?")
            alertDialog.show()
        }).show() }

        val toggle = ActionBarDrawerToggle(
                this,
                this.drawer_layout,
                this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        this.drawer_layout.addDrawerListener(toggle)

        val navHeaderView = this.drawer_layout.nav_view.getHeaderView(0)
        val navHeaderText = "${this.getText(R.string.app_name)} v${BuildConfig.VERSION_NAME}"
        navHeaderView.uxTitle.text = navHeaderText

        toggle.syncState()

        this.nav_view.setNavigationItemSelectedListener(this)

        // Check (asynchronous) database migration result
        val database: Database = Kodein.global.instance()

        val migrationResult = database.migrationResult
        if (migrationResult != null) {
            // Build error message
            var text = "${this.getText(R.string.error_database_inconsistent)}"
            text += if (migrationResult.message != null) " (${migrationResult.message})" else ""
            text += ". ${this.getText(R.string.prompt_reinstall)}"

            this.showErrorAlert(text = text, onPositiveButton = {
                this.app.terminate()
            })
        }
    }

    override fun onBackPressed() {
        if (this.drawer_layout.isDrawerOpen(GravityCompat.START)) {
            this.drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        this.drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
