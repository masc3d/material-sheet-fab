package org.deku.leoz.mobile.ui

import android.os.Bundle
import android.os.PersistableBundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.slf4j.LoggerFactory
import sx.android.IdleTimer
import java.time.DateTimeException
import java.util.*

/**
 * Base application activity
 * Created by masc on 21.08.17.
 */
abstract class BaseActivity : RxAppCompatActivity() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val idleTimer: IdleTimer by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        // IceKick integration
        unfreezeInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // IceKick integration
        freezeInstanceState(outState)
    }

    override fun onUserInteraction() {
        this.idleTimer.reset()
    }
}