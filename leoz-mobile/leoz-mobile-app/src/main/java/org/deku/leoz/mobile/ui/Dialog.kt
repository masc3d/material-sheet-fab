package org.deku.leoz.mobile.ui

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader

/**
 * Created by phpr on 29.05.2017.
 */
abstract class Dialog(@LayoutRes val dialogLayoutId: Int): RxAppCompatDialogFragment() {
    protected val log = LoggerFactory.getLogger(this.javaClass)
    protected val aidcReader: AidcReader by Kodein.global.lazy.instance()

    val builderView by lazy {
        activity.layoutInflater.inflate(dialogLayoutId, null)
    }

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