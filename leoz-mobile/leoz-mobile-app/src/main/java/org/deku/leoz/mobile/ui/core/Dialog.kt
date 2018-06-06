package org.deku.leoz.mobile.ui.core

import android.content.Context
import android.support.annotation.LayoutRes
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader

/**
 * Dialog base class
 * Created by phpr on 29.05.2017.
 */
abstract class Dialog(@LayoutRes val dialogLayoutId: Int) : RxAppCompatDialogFragment() {
    protected val log = LoggerFactory.getLogger(this.javaClass)
    protected val aidcReader: AidcReader by Kodein.global.lazy.instance()

    val builderView by lazy {
        this.activity!!.layoutInflater.inflate(dialogLayoutId, null)
    }

    override fun getContext(): Context =
            super.getContext() ?: throw IllegalStateException("Context not available")
}