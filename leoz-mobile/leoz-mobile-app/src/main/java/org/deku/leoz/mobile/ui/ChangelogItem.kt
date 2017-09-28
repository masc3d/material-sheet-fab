package org.deku.leoz.mobile.ui

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_changelog.view.*
import org.deku.leoz.mobile.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by phpr on 24.06.2017.
 */
class ChangelogItem private constructor(
        val date: String,
        val version: String,
        val entry: ChangelogEntry): AbstractFlexibleItem<ChangelogItem.ViewHolder>() {

    companion object {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
    }

    constructor(date: Date, version: String, entries: ChangelogEntry): this(date = dateFormat.format(date), version = version, entry = entries)

    data class ChangelogEntry(val title: String, val description: String)

    override fun equals(other: Any?): Boolean {
        if (other is ChangelogItem) {
            return this == other
        } else if (other is ChangelogEntry) {
            return this.entry == other
        }
        return false
    }

    override fun getLayoutRes(): Int =
            R.layout.item_changelog

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ViewHolder =
            ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<*>?,
                                holder: ViewHolder,
                                position: Int,
                                payloads: List<*>?) {
        holder.title.text = entry.title
        holder.description.text = entry.description

        this.isEnabled = true
        this.isDraggable = false
        this.isSwipeable = false
    }

    class ViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val title = view.uxTitle
        val description = view.uxDescription
    }
}