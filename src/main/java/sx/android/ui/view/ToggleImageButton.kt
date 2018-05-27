package sx.android.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.ImageButton
import org.slf4j.LoggerFactory
import sx.android.R
import sx.android.content.res.getColorOrNull
import sx.android.content.res.use
import sx.android.graphics.withTransitionTo
import sx.log.slf4j.trace


/**
 * Toggle image button
 *
 * REMARK: it would have been cleaner to derive from `CompoundButton` as it generalizes
 * `OnCheckedChangeListener`, however the feature/compat support from `ImageView` is more complex
 * to replicate.
 *
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

    /** Unselected background */
    private var unselectedBackground: Drawable? = null

    /** Unselected icon tint */
    @ColorInt
    private var unselectedTint: Int? = null

    /** Selected backgruond */
    var selectedBackground: Drawable? = null

    /** Selected icon tint */
    @ColorInt
    var selectedTint: Int? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: ToggleImageButton, isChecked: Boolean)
    }

    var onCheckedChangeListener: OnCheckedChangeListener? = null

    /** View initialization */
    private fun init(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton).use { types ->
            val checked = types.getBoolean(R.styleable.ToggleImageButton_android_checked, false)

            this.unselectedBackground = types.getDrawable(R.styleable.ToggleImageButton_background)
            this.unselectedTint = types.getColorOrNull(R.styleable.ToggleImageButton_tint)

            this.selectedTint = types.getColorOrNull(R.styleable.ToggleImageButton_selectedTint)
            this.selectedBackground = types.getDrawable(R.styleable.ToggleImageButton_selectedBackground)

            types.getBoolean(R.styleable.ToggleImageButton_android_checked, false).also {
                setChecked(checked = it, force = true)
            }
        }
    }

    //region Checkable
    override fun isChecked(): Boolean = isSelected

    private fun setChecked(checked: Boolean, force: Boolean) {
        val animate = !this.isInEditMode && !force

        if (checked != isSelected || force || this.isInEditMode) {
            isSelected = checked

            when (checked) {
                true -> {
                    this.background = selectedBackground?.let {
                        if (animate) unselectedBackground?.withTransitionTo(it, 100) else selectedBackground
                    }
                    this.selectedTint?.also { this.setIconTint(it) }
                }
                false -> {
                    this.background = unselectedBackground?.let {
                        if (animate) selectedBackground?.withTransitionTo(it, 100) else unselectedBackground
                    }
                    this.unselectedTint?.also { this.setIconTint(it) }
                }
            }

            onCheckedChangeListener?.onCheckedChanged(this, checked)
        }
    }

    override fun setChecked(checked: Boolean) {
        this.setChecked(checked = checked, force = false)
    }

    override fun toggle() {
        setChecked(!isChecked)
    }
    //endregion

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }
}