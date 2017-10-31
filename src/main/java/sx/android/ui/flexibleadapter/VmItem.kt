package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 * Flexible expandable view model header/item.
 * Can be used as both an expandable item or header.
 * Created by masc on 26.06.17.
 */
open class VmItem<VM, IVM>(
        @LayoutRes val view: Int,
        @AnyRes val variable: Int,
        val viewModel: VM,
        val blurRadius: Float = 1F,
        @IdRes val dragHandleViewId: Int = 0,
        var isExpandableOnClick: Boolean = true,
        private val isHeader: Boolean = false
) :
        AbstractExpandableItem<VmHolder, SimpleVmItem<IVM>>(),
        ISectionable<VmHolder, IHeader<VmHolder>> {

    //region ISectionable
    var _header: IHeader<VmHolder>? = null

    override fun setHeader(header: IHeader<VmHolder>?) {
        _header = header
    }

    override fun getHeader(): IHeader<VmHolder>?
            = _header
    //endregion

    /** Composite disposable for maintaining view holder subscriptions */
    private val viewHolderSubscriptions = CompositeDisposable()

    private val itemReleasedEventSubject by lazy { PublishSubject.create<Int>() }
    /** Item released event */
    val itemReleasedEvent by lazy { itemReleasedEventSubject.hide() }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: VmHolder,
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

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: VmHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)

        // Unbind view holder
        holder?.binding?.unbind()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): VmHolder {
        // Blur view support
        if (view is BlurView) {
            val rootView = adapter.recyclerView

            // Remove background. it's commonly only set for design preview, as blur view uses custom attribute `blurOverlayColor`
            view.background = null

            view.setupWith(rootView)
                    .windowBackground(adapter.recyclerView.background)
                    .blurAlgorithm(RenderScriptBlur(view.context))
                    .blurRadius(this.blurRadius)
        }

        return VmHolder(
                view = view,
                adapter = adapter,
                isStickyHeader = this.isHeader,
                dragHandleViewId = this.dragHandleViewId,
                isExpandableOnClick = isExpandableOnClick)
    }

    override fun getLayoutRes(): Int =
            this.view


    override fun equals(other: Any?): Boolean =
            this === other

    override fun hashCode(): Int =
            super.hashCode()
}