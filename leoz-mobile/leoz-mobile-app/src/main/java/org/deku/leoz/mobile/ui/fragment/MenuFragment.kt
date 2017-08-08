package org.deku.leoz.mobile.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_menu.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.activity.DeliveryActivity


class MenuFragment : Fragment<Any>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_menu, container, false)
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
            startDeliveryActivity()
        }

//        startDeliveryActivity()
    }

    private fun startDeliveryActivity() {
        val deliveryActivityIntent = Intent(context, DeliveryActivity::class.java)
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
