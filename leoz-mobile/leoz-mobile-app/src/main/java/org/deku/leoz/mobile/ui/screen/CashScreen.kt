package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.screen_cash.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory


/**
 * A simple [Fragment] subclass.
 */
class CashScreen : ScreenFragment<CashScreen.Parameters>() {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int,
            var deliveryReason: EventDeliveredReason,
            var recipient: String = ""
    )

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val stop: Stop by lazy {
        stopRepository.findById(this.parameters.stopId)
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    val cashValue: Double by lazy {
        0.0
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(org.deku.leoz.mobile.R.layout.screen_cash, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "Cash collection"
        this.scrollCollapseMode = ScrollCollapseModeType.None
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxCashValue.text = "$cashValue €"
    }

}
