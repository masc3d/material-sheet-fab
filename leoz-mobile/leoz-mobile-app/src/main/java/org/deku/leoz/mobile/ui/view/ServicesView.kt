package org.deku.leoz.mobile.ui.view

import android.content.Context
import android.databinding.Bindable
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.support.annotation.DimenRes
import android.support.annotation.Dimension
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.jetbrains.anko.imageResource
import org.slf4j.LoggerFactory
import sx.android.convertDpToPx
import java.util.*

/**
 * DEKU services view
 * Created by masc on 01.09.17.
 */
class ServicesView : LinearLayout {
    private val log = LoggerFactory.getLogger(this.javaClass)

    interface Listener {
        fun onActionItem(id: Int)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    @Dimension(unit = Dimension.PX)
    var imageMargin: Int = this.context.convertDpToPx(8.0F)

    @Dimension(unit = Dimension.PX)
    var imageSize: Int = this.context.convertDpToPx(24.0F)

    var services: List<ParcelService> = listOf()
        set(value: List<ParcelService>) {
            field = value
            update()
        }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        LinearLayout.inflate(this.context, R.layout.view_services, this)

        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.ServicesView).also {
                imageSize = it.getDimension(R.styleable.ServicesView_imageSize, this.imageSize.toFloat()).toInt()
                imageMargin = it.getDimension(R.styleable.ServicesView_imageMargin, this.imageMargin.toFloat()).toInt()
                it.recycle()
            }
        }

        this.update()
    }

    private fun update() {
        this.removeAllViews()

        when {
            isInEditMode -> {
                // Some dummy icons for edit mode
                Collections.nCopies(4, R.drawable.ic_service)
            }
            else -> {
                this.services
                        .map { service ->
                            service.mobile.icon
                        }
            }
        }
                .map { imageResource ->
                    ImageView(this.context).also {
                        it.imageResource = imageResource
                        it.layoutParams = LinearLayout.LayoutParams(
                                this.imageSize,
                                this.imageSize).also {
                        }.also {
                            it.marginEnd = this.imageMargin
                        }
                    }
                }
                .forEach {
                    this.addView(it)
                }
    }
}