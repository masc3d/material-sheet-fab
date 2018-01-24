package org.deku.leoz.mobile.ui.process

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_menu.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.TourActivity
import org.deku.leoz.mobile.ui.core.Fragment


class MenuFragment : Fragment<Any>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            // Inflate the layout for this fragment
            inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onStart() {
        super.onStart()

        this.uxDelivery.setOnClickListener {
            startDeliveryActivity()
        }

//        startDeliveryActivity()
    }

    private fun startDeliveryActivity() {
        val deliveryActivityIntent = Intent(context, TourActivity::class.java)
        startActivity(deliveryActivityIntent)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): MenuFragment {
            val fragment = MenuFragment()
            return fragment
        }
    }

}// Required empty public constructor
