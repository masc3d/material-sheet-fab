package sx.android.ui.flexibleadapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.reactivex.subjects.PublishSubject

/**
 * Flexible view model holder
 * Created by masc on 26.06.17.
 */
class FlexibleVmHolder(
        val view: View,
        val adapter: FlexibleAdapter<out IFlexible<*>>,
        isStickyHeader: Boolean = false)
    :
        FlexibleViewHolder(view, adapter, isStickyHeader) {

    private val itemReleasedEventSubject by lazy { PublishSubject.create<Int>() }
    val itemReleasedEvent by lazy { itemReleasedEventSubject.hide() }

    val binding: ViewDataBinding

    init {
        this.binding = DataBindingUtil.bind(view)
    }

    override fun onItemReleased(position: Int) {
        super.onItemReleased(position)
        this.itemReleasedEventSubject.onNext(position)
    }
}