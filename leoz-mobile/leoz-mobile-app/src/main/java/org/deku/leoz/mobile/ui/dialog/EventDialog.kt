package org.deku.leoz.mobile.ui.dialog

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import io.reactivex.subjects.PublishSubject
import io.reactivex.Observable
import org.deku.leoz.mobile.model.entity.getEventText
import org.deku.leoz.model.EventNotDeliveredReason
import org.slf4j.LoggerFactory


/**
 * Event dialog based on MaterialDialog
 * Created by phpr on 06.07.2017.
 */
class EventDialog private constructor(
        builder: EventDialog.Builder,
        /** Selected item event */
        val selectedItemEvent: Observable<EventNotDeliveredReason>
) : MaterialDialog(builder) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    interface Listener {
        fun onEventDialogItemSelected(event: EventNotDeliveredReason) {}
    }

    /**
     * Event dialog builder
     */
    class Builder(context: Context) : MaterialDialog.Builder(context) {
        private val log = LoggerFactory.getLogger(this.javaClass)

        private val selectedItemSubject = PublishSubject.create<EventNotDeliveredReason>()

        private val eventAdapter = MaterialSimpleListAdapter({ materialDialog, i, materialSimpleListItem ->
            log.debug("ONMATERIALLISTITEMSELECTED [$i]")

            val event = materialSimpleListItem.tag as EventNotDeliveredReason
            this.listener?.onEventDialogItemSelected(event)
            this.selectedItemSubject.onNext(event)
        })

        private var events = listOf<EventNotDeliveredReason>()
            set(value) {
                value.forEach {
                    val eventText = this.context.getEventText(it)
                    if (eventText != null) {
                        this@Builder.eventAdapter.add(
                                MaterialSimpleListItem.Builder(this.context)
                                        .content(eventText)
                                        .id(it.reason.id.toLong())
                                        .tag(it)
                                        .backgroundColor(Color.WHITE)
                                        .build()
                        )
                    }
                }
                field = value
            }

        private var listener: Listener? = null

        init {
            // Initialize builder
            this
                    .title("Event selection")
                    .adapter(this.eventAdapter, LinearLayoutManager(this.context))
        }

        // Builder pattern functions

        fun events(events: List<EventNotDeliveredReason>) = apply { this.events = events }
        fun listener(listener: Listener) = apply { this.listener = listener }

        override fun build(): EventDialog {
            return EventDialog(
                    selectedItemEvent = this.selectedItemSubject.hide(),
                    builder = this)
        }
    }
}