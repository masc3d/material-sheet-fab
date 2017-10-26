package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.screen_neighbour_delivery.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.jetbrains.anko.inputMethodManager
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.android.hideSoftInput
import sx.android.showSoftInput

/**
 * Neighbour delivery screen
 * Created by phpr on 10.07.2017.
 */
class NeighbourDeliveryScreen : ScreenFragment<NeighbourDeliveryScreen.Parameters>() {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    interface Listener {
        fun onNeighbourDeliveryScreenContinue(neighbourName: String)
    }

    private val listener by lazy {
        this.targetFragment as? Listener
                ?: this.parentFragment as? Listener
                ?: this.activity as? Listener
    }

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val stop: Stop by lazy {
        stopRepository
                .findById(this.parameters.stopId)
                .blockingGet()
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
        this.title = getString(R.string.title_alternativedelivery)
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.screen_neighbour_delivery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxNeighboursName.requestFocus()
        this.context.inputMethodManager.showSoftInput()

        this.uxNeighboursStreet.setAdapter(
                ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,
                        arrayOf(stop.address.street)))

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_continue,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                )
        )
    }

    override fun onResume() {
        super.onResume()

        val ovNeighborName = RxTextView
                .textChanges(this.uxNeighboursName)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovNeighborStreet = RxTextView
                .textChanges(this.uxNeighboursStreet)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovNeighborStreetNo = RxTextView
                .textChanges(this.uxNeighboursStreetNo)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovLastEditorAction = RxTextView.editorActions(this.uxNeighboursStreetNo)
                .map { Unit }
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovActionEvent = this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        // Observable which emits true/false idnicating required fields are filled or not
        val ovFieldsFilled = Observable.combineLatest(
                arrayOf(
                        ovNeighborName.map { it.length > 0 },
                        ovNeighborStreet.map { it.length > 0 },
                        ovNeighborStreetNo.map { it.length > 0 }),
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
                    this.listener?.onNeighbourDeliveryScreenContinue(
                            neighbourName = this.uxNeighboursName.text.toString()
                    )
                }
    }

}