package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.deku.leoz.mobile.model.process.DeliveryList
import sx.android.databinding.toField
import sx.rx.ObservableRxProperty

/**
 * Common parcel list header
 * Created by masc on 07.07.17.
 */
class ParcelListHeaderViewModel(
        val title: String,
        private val amountProperty: Observable<Int>,
        private val totalAmountProperty: Observable<Int>)
         : BaseObservable() {

    val amount = Observable.combineLatest(
            this.amountProperty,
            this.totalAmountProperty,
            BiFunction<Int, Int, String> { amount, totalAmount ->
                "${amount} / ${totalAmount}"
            }
    ).toField()
}