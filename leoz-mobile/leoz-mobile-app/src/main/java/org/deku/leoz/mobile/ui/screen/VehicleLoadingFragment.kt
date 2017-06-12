package org.deku.leoz.mobile.ui.screen


import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_vehicle_loading.*
import org.deku.leoz.mobile.R

import org.deku.leoz.mobile.ui.Fragment


/**
 * A simple [Fragment] subclass.
 */
class VehicleLoadingFragment : org.deku.leoz.mobile.ui.Fragment() {

    private val aidcReader: sx.android.aidc.AidcReader by com.github.salomonbrys.kodein.Kodein.global.lazy.instance()

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_vehicle_loading, container, false)
    }

    override fun onResume() {
        super.onResume()

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    processLabelScan(it.data)
                    this.uxLabelNo.setText(it.data)
                }
    }

    private fun processLabelScan(data: String) {

    }
}
