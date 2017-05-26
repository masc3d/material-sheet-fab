package org.deku.leoz.mobile.ui.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_delivery_process.*

import org.deku.leoz.mobile.R


/**
 * A simple [Fragment] subclass.
 */
class DeliveryProcessFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_delivery_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxViewPager.adapter = DeliveryProcessPageAdapter(context, childFragmentManager, listOf(
                TourOverviewFragment(),
                TourSelectionFragment()
        ))
    }

    class DeliveryProcessPageAdapter(val context: Context, fragmentManager: FragmentManager, val fragment: List<Fragment>): FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): android.support.v4.app.Fragment {
            return fragment[position]
        }

        override fun getCount(): Int {
            return fragment.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragment[position].fragmentTitle
        }

    }

}
