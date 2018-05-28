package sx.android.databinding.adapters

import android.databinding.BindingAdapter
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * View databinding adapter
 * Created by masc on 07.05.18.
 */
class ViewBindingAdapter {
    companion object {
        @BindingAdapter("android:background")
        @JvmStatic
        fun setBackground(view: View, resource: Int) {
            if (resource != 0) {
                val value = TypedValue().also {
                    view.resources.getValue(resource, it, true)
                }

                when {
                // Support for background color
                    value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT -> {
                        view.setBackgroundColor(value.data)
                    }
                // Background drawable
                    value.type == TypedValue.TYPE_REFERENCE -> {
                        view.background = ContextCompat.getDrawable(view.context, resource)
                    }
                // Other types of resource
                    else -> {
                        view.setBackgroundResource(resource)
                    }
                }
            }
        }

        @BindingAdapter("animatedVisibility")
        @JvmStatic
        fun animatedVisibility(view: View, visibility: Int) {
            if (view.visibility != visibility) {
                TransitionManager.beginDelayedTransition(view.parent as ViewGroup)
                view.visibility = visibility
            }
        }
    }
}