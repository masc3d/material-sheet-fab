package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.rx.ObservableRxProperty

/**
 * Parcel repository
 * Created by masc on 20.07.17.
 */
class ParcelRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) : ObservingRepository<ParcelEntity>(ParcelEntity::class, store) {
    private val log = LoggerFactory.getLogger(this.javaClass)

}
