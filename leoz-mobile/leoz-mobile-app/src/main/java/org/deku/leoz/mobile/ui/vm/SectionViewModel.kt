package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import sx.android.databinding.toField

/**
 * Common parcel list header
 * Created by masc on 07.07.17.
 * @param title Header title
 * @param amount Observable amount
 * @param totalAmount Observable total amount
 */
open class SectionViewModel<T>(
        @DrawableRes val icon: Int = R.drawable.ic_truck,
        @ColorRes val color: Int = R.color.colorGrey,
        @DrawableRes val background: Int = R.drawable.section_background_accent,
        val title: String,
        val showIfEmpty: Boolean = true,
        val expandOnSelection: Boolean = false,
        val items: Observable<List<T>> = Observable.empty()
) : BaseObservable() {

    private val context: Context by Kodein.global.lazy.instance()

    val amountText = items.map { it.count().toString() }.toField()

    val colorInt: Int by lazy { ContextCompat.getColor(context, color) }

    override fun toString(): String =
            "${this.javaClass.simpleName}(title=${title})"
}
