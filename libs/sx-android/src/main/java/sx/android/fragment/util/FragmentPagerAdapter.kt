package sx.android.fragment.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

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
    override fun getItem(position: Int): Fragment
            = fragments[position].createInstance()

    override fun getCount(): Int
            = fragments.count()

    /**
     * Add fragment to pager
     * @param type Type of fragment
     * @param title Title of fragment page
     * @param initializer Optional initializer code block
     */
    fun <T : Fragment> addFragment(type: Class<T>, title: CharSequence, initializer: (t: T) -> Unit = { }) {
        fragments.add(Page(type, title, initializer))
        notifyDataSetChanged()
    }

    /**
     * Remove fragment from pager
     */
    fun removeFragmentAt(index: Int) {
        fragments.removeAt(index)
        notifyDataSetChanged()
    }

    fun removeFragment(predicate: (f: Page<*>) -> Boolean) {
        this.fragments.removeAll { predicate(it) }
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence
            = fragments[position].title

    override fun getItemPosition(`object`: Any): Int =
            // Enforce refresh of fragments (in case of add/remove)
            PagerAdapter.POSITION_NONE
}