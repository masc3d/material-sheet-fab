package org.deku.leoz.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.tinsuke.icekick.freezeInstanceState
import com.tinsuke.icekick.state
import com.tinsuke.icekick.unfreezeInstanceState
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import org.deku.leoz.mobile.MainActivity
import org.deku.leoz.mobile.config.*
import org.deku.leoz.mobile.update.UpdateService
import org.slf4j.LoggerFactory

/**
 * Responsible for routing intents to activities and displaying splash screen
 * Created by masc on 27.03.14.
 */
class StartupActivity : RxAppCompatActivity() {
    val log = LoggerFactory.getLogger(this.javaClass)

    private var started: Boolean by state(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state
        this.app.unfreezeInstanceState(this)

        // Load some more heavy-weight instances on startup/splash
        val app: Application = Kodein.global.instance()
        val db: DatabaseConfiguration = Kodein.global.instance()
        val update: UpdateService = Kodein.global.instance()

        log.info("${this.app.name} v${this.app.version}")
        log.trace("Intent action ${this.intent.action}")

        val activityClass: Class<*> = MainActivity::class.java

        fun start(withAnimation: Boolean) {
            val i = Intent(this@StartupActivity, activityClass)
            i.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(i)
            if (withAnimation)
                this.overridePendingTransition(R.anim.main_fadein, R.anim.splash_fadeout)
            this.setResult(0)
            this.finish()
        }

        if (!this.started) {
            Handler().postDelayed( { start(withAnimation = true) }, 1000)
            this.started = true
        } else {
            start(withAnimation = false)
        }
    }

    override fun onPause() {
        super.onPause()

        // Save state
        this.app.freezeInstanceState(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        log.trace("ONDESTROY")
    }
}
