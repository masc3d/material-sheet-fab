package sx.android.ui.view

import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty


/**
 * Toggle group.
 * Supports `ToggleImageButton` and all `CompoundButton` derivates.
 * Created by masc on 12.05.18.
 */
class ToggleGroup : LinearLayout {
    private val log = LoggerFactory.getLogger(this.javaClass)

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {}

    interface OnCheckedChangeListener {
        fun onCheckedChanged(group: ToggleGroup, item: Checkable?)
    }

    var onCheckedChangeListener: OnCheckedChangeListener? = null

    val selectedProperty = ObservableRxProperty<Checkable?>(null)
    /** Currently selected item */
    var selected by selectedProperty

    init {
        this.selectedProperty.subscribe {
            it.value?.isChecked = true
        }
    }

    private fun onCheckedChanged(item: Checkable, isChecked: Boolean) {
        when (isChecked) {
            true -> {
                if (selected != item) {
                    val previousSelection = selected
                    selected = item
                    previousSelection?.isChecked = false

                    this.onCheckedChangeListener?.onCheckedChanged(this, item)
                }
            }
            false -> {
                if (selected == item) {
                    item.isChecked = true
                }
            }
        }
    }

    override fun onViewAdded(child: View?) {
        if (child is Checkable)
            child.isChecked = false

        when {
            child is ToggleImageButton -> {
                child.onCheckedChangeListener = object : ToggleImageButton.OnCheckedChangeListener {
                    override fun onCheckedChanged(buttonView: ToggleImageButton, isChecked: Boolean) {
                        this@ToggleGroup.onCheckedChanged(buttonView, isChecked)
                    }
                }
            }
            child is CompoundButton -> {
                child.setOnCheckedChangeListener { buttonView, isChecked ->
                    this@ToggleGroup.onCheckedChanged(buttonView, isChecked)
                }
            }
        }
    }
}
