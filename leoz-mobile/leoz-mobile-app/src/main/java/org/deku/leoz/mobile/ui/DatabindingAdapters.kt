package org.deku.leoz.mobile.ui

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView

/**
 * Created by masc on 11.07.17.
 */

/**
 * Data binding adatpers
 * Created by masc on 10.07.17.
 */
class DataBindingAdatpers {
    companion object {
        @BindingAdapter("android:src")
        @JvmStatic fun setImageUri(view: ImageView, imageUri: String?) {
            if (imageUri == null) {
                view.setImageURI(null);
            } else {
                view.setImageURI(Uri.parse(imageUri));
            }
        }

        @BindingAdapter("android:src")
        @JvmStatic fun setImageUri(view: ImageView, imageUri: Uri) {
            view.setImageURI(imageUri);
        }

        @BindingAdapter("android:src")
        @JvmStatic fun setImageDrawable(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable);
        }

        @BindingAdapter("android:src")
        @JvmStatic fun setImageResource(imageView: ImageView, resource: Int){
            imageView.setImageResource(resource);
        }

        @BindingAdapter("android:background")
        @JvmStatic fun setBackground(view: View, resource: Int){
            view.setBackgroundResource(resource)
        }
    }
}