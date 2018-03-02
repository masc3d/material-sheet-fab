package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.R
import sx.android.databinding.toField

/**
 * Counter view model
 * Created by masc on 10.07.17.
 */
class CounterViewModel(
        /** The drawable to display */
        @DrawableRes val iconRes: Int,
        /** Optional icon tint */
        @ColorRes val iconTintRes: Int = R.color.colorLighterGrey,
        /** Optional icon alpha */
        val iconAlpha: Float = 1.0F,
        /** Amount */
        val amount: Observable<Number>,
        /** Total amount (defaults to amount) */
        val totalAmount: Observable<Number> = amount,
        /** Amount formatting */
        val format: (Number) -> String = { it.toString() },
        /** Singular title */
        @StringRes val titleRes: Int = 0,
        /** Plural title (defaults to singular title) */
        @StringRes val titlePluralRes: Int = titleRes
) : BaseObservable() {
    private val context: Context by Kodein.global.lazy.instance()

    val amountTextField by lazy {
        this.amount
                .map { format(it) }
                .toField()
    }

    val totalAmountTextField by lazy {
        this.totalAmount
                .map { format(it) }
                .toField()
    }

    val amountGreaterThanZero by lazy {
        this.amount.map { it.toDouble() > 0.0 }.toField()
    }

    @delegate:StringRes
    val titleField by lazy {
        this.amount
                .map {
                    val stringRes = if (it.toDouble() > 1.0)
                        this.titlePluralRes
                    else
                        this.titleRes

                    if (stringRes > 0) context.getText(stringRes) else ""
                }
                .toField()
    }

    val iconTintColor by lazy {
        ContextCompat.getColor(context, this.iconTintRes)
    }
}