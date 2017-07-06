package org.deku.leoz.mobile.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.deku.leoz.mobile.model.Events
import org.deku.leoz.model.EventNotDeliveredReason
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01/03/2017.
 */
open class Fragment : RxAppCompatDialogFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val events: Events by Kodein.global.lazy.instance()
    private var lastEventList: List<EventNotDeliveredReason> = listOf()
    private val reasonAdapter = MaterialSimpleListAdapter(MaterialSimpleListAdapter.Callback { materialDialog, i, materialSimpleListItem ->
        log.debug("ONMATERIALLISTITEMSELECTED [$i]")
        events.thrownEventValue = lastEventList[i]
    })

    /**
     * Title
     */
    var title: String = ""

    /**
     * Activity
     */
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

    fun showFailureReasons(events: List<EventNotDeliveredReason>) {
        reasonAdapter.clear()

        events.forEach {
                    reasonAdapter.add(MaterialSimpleListItem.Builder(context)
                            .content(context.getEventText(it))
                            .backgroundColor(Color.WHITE)
                            .build())
                }

        val dialog = MaterialDialog.Builder(context)
                .title("Fehlercode")
                .adapter(reasonAdapter, null)
                .build()

        lastEventList = events

        dialog.show()
    }
}