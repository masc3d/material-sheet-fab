package org.deku.leoz.mobile.ui.dialog

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.mobile.model.getEventText
import org.deku.leoz.model.EventNotDeliveredReason
import org.slf4j.LoggerFactory

/**
 * Created by phpr on 06.07.2017.
 */
class EventDialog(context: Context, events: List<EventNotDeliveredReason>) : MaterialDialog.Builder(context) {

    private val selectedItemSubject = PublishSubject.create<Int>()
    val selectedItem = this.selectedItemSubject.hide()!!

    private val log = LoggerFactory.getLogger(this.javaClass)


    private val reasonAdapter = MaterialSimpleListAdapter(MaterialSimpleListAdapter.Callback { materialDialog, i, materialSimpleListItem ->
        log.debug("ONMATERIALLISTITEMSELECTED [$i]")
        selectedItemSubject.onNext(materialSimpleListItem.id.toInt())
    })

    init {
        events.forEach {
            reasonAdapter.add(
                    MaterialSimpleListItem.Builder(context)
                            .content(context.getEventText(it))
                            .id(it.id.toLong())
                            .backgroundColor(Color.WHITE)
                            .build()
            )
        }

        this.adapter = reasonAdapter
        this.title("Event selection")
        this.build()
    }

}