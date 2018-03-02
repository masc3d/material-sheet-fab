package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import sx.android.databinding.toField

/**
 * Counter view model
 * Created by masc on 10.07.17.
 */
class CounterViewModel(
        /** The drawable to display */
        @DrawableRes val icon: Int,
        /** Optional icon tint */
        @ColorRes val iconTint: Int = R.color.colorLighterGrey,
        /** Optional icon alpha */
        val iconAlpha: Float = 1.0F,
        /** Amount */
        val amount: Observable<Number>,
        /** Total amount (defaults to amount) */
        val totalAmount: Observable<Number> = amount,
        /** Amount formatting */
        val format: (Number) -> String = { it.toString() },
        /** Singular title */
        @StringRes val title: Int = 0,
        /** Plural title (defaults to singular title) */
        @StringRes val titlePlural: Int = title
) : BaseObservable() {
    val amountField by lazy {
        this.amount
                .map { format(it) }
                .toField()
    }

    val totalAmountField by lazy {
        this.totalAmount
                .map { format(it) }
                .toField()
    }

    @delegate:StringRes
    val titleField by lazy {
        this.amount
                .map {
                    if (it.toDouble() > 1.0)
                        this.titlePlural
                    else
                        this.title
                }
                .toField()
    }
}