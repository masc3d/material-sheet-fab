package sx.android.databinding

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView

/**
 * Data binding adapters
 *
 * IMPORTANT: those don't seem to be work when placed inside a library and
 * currently have to be copied to application level
 *
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
        @JvmStatic fun setImageUri(view: ImageView, imageUri: Uri ) {
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
    }
}