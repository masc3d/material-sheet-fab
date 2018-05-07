package sx.android.databinding.adapters

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView

/**
 * Image view binding adapter
 * Created by masc on 07.05.18.
 */
class ImageViewBindingAdapter {
    companion object {
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

    }
}