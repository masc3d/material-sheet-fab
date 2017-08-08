package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import io.reactivex.Observable
import org.deku.leoz.mobile.model.entity.ParcelEntity

/**
 * Created by masc on 08.08.17.
 */
interface SectionViewModel<T> {
    val items: Observable<List<T>>
    val showIfEmpty: Boolean
}
