package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.KeyEvent
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
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.screen_neighbour_delivery.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.model.EventDeliveredReason
import org.jetbrains.anko.inputMethodManager
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.android.hideSoftInput

/**
 * Created by phpr on 10.07.2017.
 */
class NeighbourDeliveryScreen : ScreenFragment<NeighbourDeliveryScreen.Parameters>() {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val stop: Stop by lazy {
        stopRepository.findById(this.parameters.stopId)
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
        this.scrollCollapseMode = ScrollCollapseModeType.EnterAlways
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.screen_neighbour_delivery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxNeighboursName.requestFocus()

        this.uxNeighboursStreet.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, arrayOf(stop.address.street)))

        this.uxContinue.setOnClickListener {
            this.activity.showScreen(
                    SignatureScreen().also {
                        it.parameters = SignatureScreen.Parameters(
                                stopId = this.stop.id,
                                deliveryReason = EventDeliveredReason.NEIGHBOR,
                                recipient = this.uxNeighboursName.text.toString()
                        )
                    }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        val action = RxTextView.editorActions(this.uxNeighboursStreetNo)
                        .map { Unit }
                        .replay(1)
                        .refCount()
                        .doOnNext {
                            this.context.inputMethodManager.hideSoftInput()
                        }

        action
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())

    }

}