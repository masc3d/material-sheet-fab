package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.deku.leoz.mobile.R


/**
 * A simple [Fragment] subclass.
 */
class CashScreen : android.support.v4.app.Fragment() {


    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(org.deku.leoz.mobile.R.layout.fragment_cash, container, false)
    }

}
