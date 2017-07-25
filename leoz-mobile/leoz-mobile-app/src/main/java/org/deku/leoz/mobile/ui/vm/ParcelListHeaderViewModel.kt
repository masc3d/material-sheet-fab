package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import sx.android.databinding.toField

/**
 * Common parcel list header
 * Created by masc on 07.07.17.
 * @param title Header title
 * @param amount Observable amount
 * @param totalAmount Observable total amount
 */
class ParcelListHeaderViewModel(
        val title: String,
        private val amount: Observable<Int>
)
    : BaseObservable() {

    private val expandClickedEventSubject = PublishSubject.create<Unit>()
    val expandClickedEvent = expandClickedEventSubject.hide()

    val amountText = amount.map { it.toString() }.toField()

    fun onClick(view: View) {
        expandClickedEventSubject.onNext(Unit)
    }
}