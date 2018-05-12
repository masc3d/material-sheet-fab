package org.deku.leoz.mobile.ui.process.tour.stop

import android.databinding.DataBindingUtil
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
import io.reactivex.rxkotlin.combineLatest
import kotlinx.android.synthetic.main.screen_tour_stop_recipient.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemServiceAcknowledgementBinding
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.TourStop
import org.deku.leoz.mobile.ui.core.Headers
import org.deku.leoz.mobile.ui.core.ScreenFragment
import sx.android.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.ServiceViewModel
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.SalutationType
import org.jetbrains.anko.inputMethodManager
import org.slf4j.LoggerFactory
import sx.android.databinding.toObservable
import sx.android.view.hideSoftInput
import sx.android.view.showSoftInput
import sx.rx.just

/**
 * Neighbour delivery screen
 * Created by phpr on 10.07.2017.
 */
class RecipientScreen : ScreenFragment<Any>() {

    interface Listener {
        fun onRecipientScreenComplete(
                recipientName: String,
                recipientSalutation: SalutationType? = null,
                recipientStreet: String? = null,
                recipientStreetNo: String? = null
        )
    }

    private val listener by listenerDelegate<Listener>()

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val tour: Tour by Kodein.global.lazy.instance()

    private val tourStop: TourStop by lazy {
        this.tour.activeStop ?: throw IllegalArgumentException("Active stop not set")
    }

    /** Indicates if street entry is required */
    private val requiresStreet: Boolean by lazy { this.tourStop.deliveredReason == EventDeliveredReason.NEIGHBOR }

    /** Acknowledgement view models */
    private val acknowledgements by lazy {
        this.tourStop.services
                .mapNotNull { service ->
                    service.mobile.ackMessageText(this.context)
                            ?.let {
                                ServiceViewModel(context = this.context, service = service)
                            }
                }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.acknowledgements.count() == 0) {
            this.uxName.requestFocus()
            this.context.inputMethodManager.showSoftInput()
        }

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

        this.acknowledgements.forEach { vm ->
            // Create and bind acknowledgement views
            val ackView = this.layoutInflater.inflate(R.layout.item_service_acknowledgement, null, false)

            DataBindingUtil.bind<ItemServiceAcknowledgementBinding>(ackView)?.also {
                it.setVariable(BR.service, vm)
                this.uxAcknowledges.addView(it.root)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Reset acknowledges on resume
        this.acknowledgements.forEach { it.confirmed.set(false) }

        // Reactive validation and processing
        val ovAcknowledgesConfirmed = if (this.acknowledgements.count() > 0)
            this.acknowledgements
                    .map { it.confirmed.toObservable() }
                    .combineLatest { it.all { it == true } }
                    .doOnNext {
                        if (it) {
                            this.uxName.requestFocus()
                            if (this.context.inputMethodManager.isActive)
                                this.context.inputMethodManager.showSoftInput()
                        }
                    }
        else
            true.just()


        val ovNeighborName = RxTextView
                .textChanges(this.uxName)

        val ovNeighborStreet = RxTextView
                .textChanges(this.uxStreet)

        val ovNeighborStreetNo = RxTextView
                .textChanges(this.uxStreetNo)

        val ovLastEditorAction = when (this.requiresStreet) {
            true -> RxTextView.editorActions(this.uxStreetNo)
            else -> RxTextView.editorActions(this.uxName)
        }
                .filter { it == EditorInfo.IME_ACTION_DONE }
                .map { Unit }

        val ovActionEvent = this.activity.actionEvent

        // Observable which emits true/false idnicating required fields are filled or not
        val ovFieldsFilled = listOf(
                ovNeighborName.map { it.length > 0 },
                if (this.requiresStreet) ovNeighborStreet.map { it.length > 0 } else true.just(),
                if (this.requiresStreet) ovNeighborStreetNo.map { it.length > 0 } else true.just()
        )
                .plus(
                        ovAcknowledgesConfirmed
                )
                .combineLatest { it.all { it == true } }
                .distinctUntilChanged()

        // Action button visibility
        ovFieldsFilled
                .bindUntilEvent(this, FragmentEvent.PAUSE)
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
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.context.inputMethodManager.hideSoftInput()
                    this.listener?.onRecipientScreenComplete(
                            recipientName = this.uxName.text.toString(),
                            recipientSalutation = when (this.uxSalutation.checkedRadioButtonId) {
                                R.id.uxSalutationMs -> SalutationType.Female
                                else -> SalutationType.Male
                            },
                            recipientStreet = if (this.requiresStreet) this.uxStreet.text.toString() else null,
                            recipientStreetNo = if (this.requiresStreet) this.uxStreetNo.text.toString() else null
                    )
                }
    }

}