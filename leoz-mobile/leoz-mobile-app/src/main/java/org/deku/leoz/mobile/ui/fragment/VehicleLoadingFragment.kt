package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_vehicle_loading.*

import org.deku.leoz.mobile.R
import sx.android.aidc.AidcReader


/**
 * A simple [Fragment] subclass.
 */
class VehicleLoadingFragment : Fragment() {

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
