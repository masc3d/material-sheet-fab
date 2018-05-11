package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.MarginLayoutParamsCompat
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService
import org.jetbrains.anko.imageResource
import org.slf4j.LoggerFactory
import sx.android.convertDpToPx
import sx.android.ui.view.setIconTint
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
    private var imageMargin: Int = this.context.convertDpToPx(8.0F)

    @Dimension(unit = Dimension.PX)
    private var imageSize: Int = this.context.convertDpToPx(24.0F)

    private var imageTint: Int = Color.BLACK
        @ColorInt get
        @ColorInt set

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
                imageTint = it.getColor(R.styleable.ServicesView_imageTint, ResourcesCompat.getColor(this.resources, android.R.color.black, null))
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
                        it.setIconTint(this.imageTint)
                        it.layoutParams = LinearLayout.LayoutParams(
                                this.imageSize,
                                this.imageSize).also {
                            MarginLayoutParamsCompat.setMarginEnd(it, this.imageMargin)
                        }
                    }
                }
                .forEach {
                    this.addView(it)
                }
    }
}