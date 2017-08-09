package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.ParcelEntity
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
        @ColorRes val color: Int = R.color.colorAccent,
        @DrawableRes val background: Int = R.drawable.section_background_accent,
        val isSelectable: Boolean = true,
        val title: String,
        val showIfEmpty: Boolean = true,
        val items: Observable<List<T>>
)
    : BaseObservable() {

    val amountText = items.map { it.count().toString() }.toField()
}
