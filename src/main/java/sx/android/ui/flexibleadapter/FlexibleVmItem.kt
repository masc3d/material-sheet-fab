package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 * Flexible view model item
 * Created by masc on 26.06.17.
 * @property view The view layout resource ID
 * @property variable View model binding ID
 * @property viewModel The view model
 * @property dragHandleViewId The view id of the drag handle (wheb moving items is supported)
 * @property isTransitionsEnabled Begins a transition everytime the binding is rebound (eg. bound fields change)
 */
open class FlexibleVmItem<VM>(
        @LayoutRes val view: Int,
        @AnyRes val variable: Int,
        val viewModel: VM,
        @IdRes private val dragHandleViewId: Int = 0,
        val isTransitionsEnabled: Boolean = false
) : AbstractFlexibleItem<FlexibleVmHolder>() {

    override fun equals(other: Any?): Boolean = (this === other)

    override fun hashCode(): Int = super.hashCode()

    /** Composite disposable for maintaining view holder subscriptions */
    private val viewHolderSubscriptions = CompositeDisposable()

    private val itemReleasedEventSubject by lazy { PublishSubject.create<Int>() }
    /** Item released event */
    val itemReleasedEvent by lazy { itemReleasedEventSubject.hide() }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: FlexibleVmHolder,
            position: Int,
            payloads: MutableList<Any?>) {

        viewHolder.binding.setVariable(this.variable, this.viewModel)
        viewHolder.binding.executePendingBindings()

        // Dispose previous view holder subscriptions
        viewHolderSubscriptions.clear()

        // Register and pass-through view holder events
        viewHolderSubscriptions.add(
                viewHolder.itemReleasedEvent
                        .subscribe {
                            itemReleasedEventSubject.onNext(it)
                        }
        )
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: FlexibleVmHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)

        // Unbind view holder
        holder?.binding?.unbind()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FlexibleVmHolder {
        val holder = FlexibleVmHolder(
                view = view,
                adapter = adapter,
                dragHandleViewId = dragHandleViewId,
                isTransitionsEnabled = isTransitionsEnabled)

        return holder
    }

    override fun getLayoutRes(): Int = this.view
}