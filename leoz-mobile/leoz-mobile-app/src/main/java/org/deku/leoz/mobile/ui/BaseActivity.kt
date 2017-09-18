package org.deku.leoz.mobile.ui

import android.os.Bundle
import android.os.PersistableBundle
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * Base application activity
 * Created by masc on 21.08.17.
 */
abstract class BaseActivity : RxAppCompatActivity() {

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
}