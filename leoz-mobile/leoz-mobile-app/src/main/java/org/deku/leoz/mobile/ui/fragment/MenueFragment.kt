package org.deku.leoz.mobile.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_aidc_camera.*
import kotlinx.android.synthetic.main.fragment_menue.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import sx.android.fragment.util.withTransaction


class MenueFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_menue, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()

        this.uxDelivery.setOnClickListener {
            val deliveryActivityIntent = Intent(context, DeliveryActivity::class.java)
            startActivity(deliveryActivityIntent)
        }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment MenueFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): MenueFragment {
            val fragment = MenueFragment()
            return fragment
        }
    }

}// Required empty public constructor
