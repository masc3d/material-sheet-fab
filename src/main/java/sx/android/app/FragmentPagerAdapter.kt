package sx.android.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup

/**
 * Fragment pager adapter implementation
 * Created by masc on 11/07/16.<
 */
class FragmentPagerAdapter(val manager: FragmentManager) : FragmentPagerAdapter(manager) {
    var baseId = 0L

    data class PagerFragment<T : Fragment>(val fragmentType: Class<T>, val title: CharSequence)  {
    }

    private val fragmentList = arrayListOf<PagerFragment<*>>()

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position).fragmentType.newInstance()
    }

    override fun getCount(): Int {
        return fragmentList.count()
    }

    fun <T : Fragment> addFragment(fragment: Class<T>, title: CharSequence) {
        fragmentList.add(PagerFragment(fragment, title))
        notifyDataSetChanged()
    }

    fun removeFragmentAt(index: Int) {
        baseId += this.fragmentList.count() + 1
        fragmentList.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentList.get(position).title
    }

    override fun getItemId(position: Int): Long {
        return baseId + position
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }
}