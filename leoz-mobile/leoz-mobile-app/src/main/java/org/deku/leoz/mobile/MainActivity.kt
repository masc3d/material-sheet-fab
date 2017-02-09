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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.deku.leoz.mobile.prototype.activities.Proto_MainActivity

class MainActivity : RxAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_main)
        setSupportActionBar(this.toolbar)

        this.fab.setOnClickListener { view -> Snackbar.make(view, "Call Supervisor for assistance?", Snackbar.LENGTH_LONG).setAction("Action", {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    var mIntent : Intent
                    mIntent = Intent(this, Proto_MainActivity::class.java)
                    startActivity(mIntent)
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
        toggle.syncState()

        this.nav_view.setNavigationItemSelectedListener(this)
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
        this.lblNavAppVersion.setText("${applicationContext.getString(R.string.AppVersion_suff)} ${BuildConfig.VERSION_CODE}")
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
