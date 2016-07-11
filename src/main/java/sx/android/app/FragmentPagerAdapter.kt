package sx.android.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Fragment pager adapter implementation
 * Created by masc on 11/07/16.
 */
class FragmentPagerAdapter(val manager: FragmentManager) : FragmentPagerAdapter(manager) {
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

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        super.destroyItem(container, position, `object`)

        // Base class method merely detaches fragments, which will lead
        // to inconsistencies of .fragmentList with the fragment manager.
        // Fragment instances which are not known to this adapter should be removed:
        val fragment = `object` as Fragment
        if (this.fragmentList.find { it.fragment === fragment } == null) {
            val t = manager.beginTransaction()
            t.remove(fragment)
            t.commit()
        }
    }

    fun removeFragmentAt(index: Int) {
        val fragment = fragmentList[index].fragment
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