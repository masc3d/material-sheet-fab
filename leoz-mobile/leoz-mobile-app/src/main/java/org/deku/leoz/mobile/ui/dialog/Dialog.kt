package org.deku.leoz.mobile.ui.dialog

import android.content.Context
import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.slf4j.LoggerFactory

/**
 * Created by phpr on 29.05.2017.
 */
open class Dialog: RxAppCompatDialogFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        log.debug("ONATTACH")
    }

    override fun onDetach() {
        super.onDetach()
        log.debug("ONDETACH")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.debug("ONCREATE")
    }

    override fun onDestroy() {
        super.onDestroy()
        log.debug("ONDESTROY")
    }

    override fun onPause() {
        super.onPause()
        log.debug("ONPAUSE")
    }

    override fun onResume() {
        super.onResume()
        log.debug("ONRESUME")
    }
}