package sx.android.databinding.adapters

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.widget.TextView

/**
 * Created by masc on 07.05.18.
 */
class TextViewBindingAdapter {
    companion object {
        @BindingAdapter("android:text")
        @JvmStatic
        fun setText(view: TextView, value: Int?) {
            view.text = value?.toString()
        }

        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getText(view: TextView): Int? =
                try {
                    view.text?.toString()?.toInt()
                } catch (e: Throwable) {
                    null
                }

    }
}