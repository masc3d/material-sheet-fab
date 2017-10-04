package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes

/**
 * Created by masc on 10.07.17.
 */
class CounterViewModel(
        @DrawableRes val drawableRes: Int,
        val amount: ObservableField<String>,
        val totalAmount: ObservableField<String>
) : BaseObservable()