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

    data class PagerFragment<T : Fragment>(
            val type: Class<T>,
            val title: CharSequence,
            val initializer: (t: T) -> Unit) {

        fun createInstance(): T {
            val instance = this.type.newInstance()
            this.initializer(instance)
            return instance
        }
    }

    private val fragments = arrayListOf<PagerFragment<*>>()

    override fun getItem(position: Int): Fragment {
        return fragments[position].createInstance()
    }

    override fun getCount(): Int {
        return fragments.count()
    }

    /**
     * Add fragment to pager
     * @param type Type of fragment
     * @param title Title of fragment page
     * @param initializer Optional initializer code block
     */
    fun <T : Fragment> addFragment(fragment: Class<T>, title: CharSequence, initializer: (t: T) -> Unit = { }) {
        fragments.add(PagerFragment(fragment, title, initializer))
        notifyDataSetChanged()
    }

    /**
     * Remove fragment from pager
     */
    fun removeFragmentAt(index: Int) {
        baseId += this.fragments.count() + 1
        fragments.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments.get(position).title
    }

    override fun getItemId(position: Int): Long {
        return baseId + position
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }
}