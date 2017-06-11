package org.deku.leoz.mobile.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.deku.leoz.mobile.ui.activity.Activity
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01/03/2017.
 */
open class Fragment(var fragmentTitle: String = "") : RxAppCompatDialogFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        if (fragmentTitle.isNullOrBlank()) {
            fragmentTitle = javaClass.simpleName
        }
    }

    val activity: Activity
        get() = super.getActivity() as Activity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        log.trace("ONATTACH")
    }

    override fun onDetach() {
        super.onDetach()
        log.trace("ONDETACH")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.trace("ONCREATE")
    }

    override fun onDestroy() {
        super.onDestroy()
        log.trace("ONDESTROY")
    }

    override fun onPause() {
        super.onPause()
        log.trace("ONPAUSE")
    }

    override fun onResume() {
        super.onResume()
        log.trace("ONRESUME")
    }
}