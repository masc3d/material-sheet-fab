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
import kotlinx.android.synthetic.main.screen_neighbour_delivery.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.model.EventDeliveredReason

/**
 * Created by phpr on 10.07.2017.
 */
class NeighbourDeliveryScreen : ScreenFragment<Any>() {

    private lateinit var stop: Stop

    companion object {
        private val db: Database by Kodein.global.lazy.instance()

        fun create(stopId: Int): NeighbourDeliveryScreen {
            val s = NeighbourDeliveryScreen()
            s.stop = db.store.select(StopEntity::class)
                        .where(StopEntity.ID.eq(stopId))
                        .get()
                        .first()
            return s
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.screen_neighbour_delivery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxNeighboursStreet.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, arrayOf(stop.address.street)))

        this.uxContinue.setOnClickListener {
            this.activity.showScreen(
                    SignatureScreen().also {
                        it.parameters = SignatureScreen.Parameters(
                                stopId = this.stop.id,
                                deliveryReason = EventDeliveredReason.Neighbor,
                                recipient = this.uxNeighboursName.text.toString()
                        )
                    }
            )
        }
    }

}