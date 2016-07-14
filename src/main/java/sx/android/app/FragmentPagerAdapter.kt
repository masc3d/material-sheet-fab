package sx.android.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import java.util.*

/**
 * Fragment state pager adapter implementation with support for adding and removing fragments
 * Created by masc on 11/07/16.
 */
class FragmentPagerAdapter(val manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

    /**
     * Page representing a fragment.
     * Contains only type of fragment and provides instances on demand
     */
    data class Page<T : Fragment>(
            val type: Class<T>,
            val title: CharSequence,
            val initializer: (t: T) -> Unit) {

        fun createInstance(): T {
            val instance = this.type.newInstance()
            this.initializer(instance)
            return instance
        }
    }

    /**
     * List of pager fragment instances
     */
    private val fragments = arrayListOf<Page<*>>()

    /**
     * Provides fragment instances to underlying pager adapter
     */
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
        fragments.add(Page(fragment, title, initializer))
        notifyDataSetChanged()
    }

    /**
     * Remove fragment from pager
     */
    fun removeFragmentAt(index: Int) {
        fragments.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position].title
    }

    override fun getItemPosition(`object`: Any?): Int {
        // Enforce refresh of fragments (in case of add/remove)
        return PagerAdapter.POSITION_NONE
    }
}