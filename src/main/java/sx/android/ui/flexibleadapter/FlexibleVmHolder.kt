package sx.android.ui.flexibleadapter

import android.databinding.DataBindingUtil
import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionListenerAdapter
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory

/**
 * Flexible view model holder
 * Created by masc on 26.06.17.
 * @property view The view this holder refers to
 * @property adapter The adapter this holder belongs to
 * @property isStickyHeader Enabled sticky header behavior (when this holder refers to a header item)
 * @property beginDelayedTransition Begins a delayed transition initially
 * @param dragHandleViewId The view id of the drag handle (wheb moving items is supported)
 */
class FlexibleVmHolder(
        val view: View,
        val adapter: FlexibleAdapter<out IFlexible<*>>,
        val isStickyHeader: Boolean = false,
        val isExpandableOnClick: Boolean = true,
        @IdRes dragHandleViewId: Int = 0
) :
        ExpandableViewHolder(view, adapter, isStickyHeader) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val itemReleasedEventSubject by lazy { PublishSubject.create<Int>() }
    val itemReleasedEvent by lazy { itemReleasedEventSubject.hide() }

    val binding: ViewDataBinding

    private var beginDelayedTransition = false

    init {
        this.binding = DataBindingUtil.bind(view)

        this.binding.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
            /** Track transition animations to avoid overlapping transitions */
            private var started = false

            override fun onPreBind(binding: ViewDataBinding): Boolean {
                if (!started && this@FlexibleVmHolder.beginDelayedTransition) {
                    TransitionManager.beginDelayedTransition(binding.root as ViewGroup, AutoTransition().also {
                        it.addListener(object : TransitionListenerAdapter() {
                            override fun onTransitionStart(transition: Transition) {
                                started = true
                            }

                            override fun onTransitionCancel(transition: Transition) {
                                started = false
                                this@FlexibleVmHolder.beginDelayedTransition = false
                            }

                            override fun onTransitionEnd(transition: Transition) {
                                started = false
                                this@FlexibleVmHolder.beginDelayedTransition = false
                            }
                        })
                    })
                }

                return super.onPreBind(binding)
            }
        })

        if (dragHandleViewId != 0) {
            this.setDragHandleView(view.findViewById(dragHandleViewId))
        }
    }

    /**
     * Starts a delayed transition for the next binding cycle
     */
    fun beginDelayedTransition() {
        this.beginDelayedTransition = true
    }

    override fun toggleExpansion() {
        if (this.isExpandableOnClick)
            super.toggleExpansion()
    }

    override fun onItemReleased(position: Int) {
        super.onItemReleased(position)
        this.itemReleasedEventSubject.onNext(position)
    }
}