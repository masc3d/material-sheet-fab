package org.deku.leoz.mobile.ui.core.extension

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.slf4j.LoggerFactory
import sx.android.view.CircularProgressView
import sx.log.slf4j.trace


/**
 * Data binding adatpers
 * Created by masc on 10.07.17.
 */
class DataBindingAdatpers {
    companion object {
        private val log = LoggerFactory.getLogger(DataBindingAdatpers::class.java)

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageUri(view: ImageView, imageUri: String?) {
            if (imageUri == null) {
                view.setImageURI(null)
            } else {
                view.setImageURI(Uri.parse(imageUri))
            }
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageUri(view: ImageView, imageUri: Uri) {
            view.setImageURI(imageUri)
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageDrawable(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable)
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

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

        @BindingAdapter("cpv_progress")
        @JvmStatic
        fun setProgress(view: CircularProgressView, progress: Float) {
            view.progress = progress
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText(view: TextView, value: Int?) {
            view.text = value?.toString()
        }

        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getText(view: TextView): Int? =
                try { view.text?.toString()?.toInt() } catch (e: Throwable) { null }
    }
}