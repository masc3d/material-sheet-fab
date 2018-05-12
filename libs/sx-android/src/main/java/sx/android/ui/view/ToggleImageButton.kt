package sx.android.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.transition.TransitionManager
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.ImageButton
import org.slf4j.LoggerFactory
import sx.android.R
import sx.android.content.res.getColorOrNull
import sx.android.content.res.use


/**
 * Toggle image button
 * Created by masc on 12.05.18.
 */
class ToggleImageButton : ImageButton, Checkable {
    private val log = LoggerFactory.getLogger(this.javaClass)

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    @ColorInt
    private var unselectedTint: Int? = null

    private var unselectedBackground: Drawable? = null

    var selectedBackground: Drawable? = null

    @ColorInt
    var selectedTint: Int? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: ToggleImageButton, isChecked: Boolean)
    }

    var onCheckedChangeListener: OnCheckedChangeListener? = null

    private fun init(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton).use { types ->
            val checked = types.getBoolean(R.styleable.ToggleImageButton_android_checked, false)

            this.unselectedBackground = types.getDrawable(R.styleable.ToggleImageButton_background)
            this.unselectedTint = types.getColorOrNull(R.styleable.ToggleImageButton_tint)

            this.selectedTint = types.getColorOrNull(R.styleable.ToggleImageButton_selectedTint)
            this.selectedBackground = types.getDrawable(R.styleable.ToggleImageButton_selectedBackground)

            setChecked(checked)
        }
    }

    override fun isChecked(): Boolean = isSelected

    override fun setChecked(checked: Boolean) {
        isSelected = checked

        when (checked) {
            true -> {
                this.selectedBackground?.also { this.background = it }
                this.selectedTint?.also { this.setIconTint(it) }
            }
            false -> {
                this.background = this.unselectedBackground
                this.unselectedTint?.also { this.setIconTint(it) }
            }
        }

        onCheckedChangeListener?.onCheckedChanged(this, checked)
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }
}