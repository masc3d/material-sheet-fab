package sx.android.databinding.adapters

import android.databinding.BindingAdapter
import sx.android.view.CircularProgressView

/**
 * Created by masc on 07.05.18.
 */
class CircularProgressViewBindingAdapter {
    companion object {
        @BindingAdapter("cpv_progress")
        @JvmStatic
        fun setProgress(view: CircularProgressView, progress: Float) {
            view.progress = progress
        }
    }
}