package sx.android.ui.view

import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import org.slf4j.LoggerFactory


/**
 * Toggle group.
 * Supports `ToggleImageButton` and all `CompoundButton` derivates.
 * Created by masc on 12.05.18.
 */

// TODO: dabinding support (following setup is yet untesed)
//@BindingMethods(
//        BindingMethod(
//                type = ToggleGroup::class,
//                attribute = "onCheckedChanged", method = "setOnCheckedChangedListener"
//        )
//)
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
        fun onCheckedChanged(group: ToggleGroup, @IdRes checkedId: Int)
    }

    var onCheckedChangeListener: OnCheckedChangeListener? = null

    private var selected: Checkable? = null

    private fun onCheckedChanged(item: Checkable, isChecked: Boolean) {
        val view = item as View

        when (isChecked) {
            true -> {
                if (selected != item) {
                    val previousSelection = selected
                    selected = item
                    previousSelection?.isChecked = false

                    this.onCheckedChangeListener?.onCheckedChanged(this, view.id)
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
