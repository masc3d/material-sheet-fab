package sx.android.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Fragment pager adapter implementation
 * Created by masc on 11/07/16.
 */
class FragmentPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    data class PagerFragment(val fragment: Fragment, val title: CharSequence) {
    }

    private val fragmentList = arrayListOf<PagerFragment>()

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position).fragment
    }

    override fun getCount(): Int {
        return fragmentList.count()
    }

    fun addFragment(fragment: Fragment, title: CharSequence) {
        fragmentList.add(PagerFragment(fragment, title))
        notifyDataSetChanged()
    }

    fun removeFragmentAt(index: Int) {
        fragmentList.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentList.get(position).title
    }

    override fun getItemPosition(`object`: Any?): Int {
        val index = fragmentList.indexOfFirst { it.fragment === `object` }
        return if (index >= 0) index else POSITION_NONE
    }
}