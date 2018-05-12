package sx.android.ui.view

import android.content.Context
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import org.jetbrains.anko.childrenSequence
import org.slf4j.LoggerFactory


/**
 * Toggle group. Any `Checkable` widget is supported.
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

    private var selected: Checkable? = null

    private fun init(attrs: AttributeSet) {

    }

    private fun onCheckedChanged(item: Checkable, isChecked: Boolean) {

        when (isChecked) {
            true -> {
                if (selected != item) {
                    TransitionManager.beginDelayedTransition(this, AutoTransition())

                    val previousSelection = selected
                    selected = item
                    previousSelection?.isChecked = false
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