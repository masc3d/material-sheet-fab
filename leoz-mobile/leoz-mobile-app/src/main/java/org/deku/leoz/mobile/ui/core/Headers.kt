package org.deku.leoz.mobile.ui.core

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.R
import sx.android.copyWithOpacity

/**
 * Header drawable factory
 * Created by masc on 19.09.17.
 */
object Headers {
    private val context: Context by Kodein.global.lazy.instance()

    private fun createHeaderDrawable(@DrawableRes bitmapRes: Int, alpha: Double): Drawable {
        return BitmapDrawable(
                context.resources,
                BitmapFactory.decodeResource(context.resources, bitmapRes)
                        .copyWithOpacity(alpha))
    }

    val street: Drawable
        get() = createHeaderDrawable(R.mipmap.img_street_1, 0.8)

    val cash: Drawable
        get() = createHeaderDrawable(R.mipmap.img_money, 0.3)

    val parcels: Drawable
        get() = createHeaderDrawable(R.mipmap.img_parcels_1, 0.3)

    val delivery: Drawable
        get() = createHeaderDrawable(R.mipmap.img_deliver_1, 0.5)
}