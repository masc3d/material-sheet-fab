package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment


class DeliveryFailureScreen : Fragment<Any>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_failure, container, false)
    }
}
