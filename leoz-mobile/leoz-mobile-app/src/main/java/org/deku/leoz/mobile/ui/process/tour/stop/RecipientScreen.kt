package org.deku.leoz.mobile.ui.process.tour.stop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.screen_tour_stop_recipient.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.TourStop
import org.deku.leoz.mobile.ui.core.Headers
import org.deku.leoz.mobile.ui.core.ScreenFragment
import org.deku.leoz.mobile.ui.core.view.ActionItem
import org.deku.leoz.model.EventDeliveredReason
import org.jetbrains.anko.inputMethodManager
import org.slf4j.LoggerFactory
import sx.android.hideSoftInput
import sx.android.showSoftInput
import sx.rx.just

/**
 * Neighbour delivery screen
 * Created by phpr on 10.07.2017.
 */
class RecipientScreen : ScreenFragment<Any>() {

    interface Listener {
        fun onRecipientScreenComplete(recipientName: String)
    }

    private val listener by listenerDelegate<Listener>()

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val tour: Tour by Kodein.global.lazy.instance()

    private val tourStop: TourStop by lazy {
        this.tour.activeStop ?: throw IllegalArgumentException("Active stop not set")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true

        this.headerImage = Headers.delivery
        this.title = when (this.tourStop.deliveredReason) {
            EventDeliveredReason.NEIGHBOR -> getString(R.string.alternative_recipient)
            else -> getString(R.string.recipient)
        }
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.screen_tour_stop_recipient, container, false)

    private val requiresStreet: Boolean by lazy { this.tourStop.deliveredReason == EventDeliveredReason.NEIGHBOR }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxName.requestFocus()
        this.context.inputMethodManager.showSoftInput()

        this.uxStreet.setAdapter(
                ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,
                        arrayOf(tourStop.entity.address.street)))

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_continue,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                )
        )

        if (!this.requiresStreet) {
            this.uxStreetContainer.visibility = View.GONE
            this.uxName.imeOptions = EditorInfo.IME_ACTION_DONE
            this.uxStreet.imeOptions = EditorInfo.IME_ACTION_NONE
            this.uxStreetNo.imeOptions = EditorInfo.IME_ACTION_NONE
        }
    }

    override fun onResume() {
        super.onResume()

        val ovNeighborName = RxTextView
                .textChanges(this.uxName)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovNeighborStreet = RxTextView
                .textChanges(this.uxStreet)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovNeighborStreetNo = RxTextView
                .textChanges(this.uxStreetNo)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovLastEditorAction = when (this.requiresStreet) {
            true -> RxTextView.editorActions(this.uxStreetNo)
            else -> RxTextView.editorActions(this.uxName)
        }
                .filter { it == EditorInfo.IME_ACTION_DONE }
                .map { Unit }
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovActionEvent = this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        // Observable which emits true/false idnicating required fields are filled or not
        val ovFieldsFilled = Observable.combineLatest(
                arrayOf(
                        ovNeighborName.map { it.length > 0 },
                        if (this.requiresStreet) ovNeighborStreet.map { it.length > 0 } else true.just(),
                        if (this.requiresStreet) ovNeighborStreetNo.map { it.length > 0 } else true.just()
                ),
                { a: Array<Any> -> a.all { it == true } }
        )
                .distinctUntilChanged()

        // Action button visibility
        ovFieldsFilled
                .subscribe {
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_continue }
                                .visible = it
                    }
                }

        Observable.mergeArray(
                // Last editor action combined with form fill indicator
                ovLastEditorAction
                        .withLatestFrom(
                                ovFieldsFilled,
                                BiFunction { _: Unit, b: Boolean -> b })
                        .filter { it == true },
                // Action button event
                ovActionEvent
                        .filter { it == R.id.action_continue }
        )
                .subscribe {
                    this.context.inputMethodManager.hideSoftInput()
                    this.listener?.onRecipientScreenComplete(
                            recipientName = this.uxName.text.toString()
                    )
                }
    }

}