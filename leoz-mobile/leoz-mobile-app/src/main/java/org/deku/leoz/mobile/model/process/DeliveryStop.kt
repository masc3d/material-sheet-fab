package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.requery.ObservableQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.behave
import sx.rx.bind

/**
 * Mobile delivery stop
 * Created by masc on 08.08.17.
 */
class DeliveryStop(
        val entity: StopEntity) : CompositeDisposableSupplier {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val compositeDisposable = CompositeDisposable()

    private val db: Database by Kodein.global.lazy.instance()

    /**
     * Observable stop query
     */
    private val stopQuery = ObservableQuery<StopEntity>(
            name = "Delivery stop",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.ID.eq(entity.id))
                    .get()
    )
            .bind(this)

    private val stopParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Delivery stop parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.ORDER_ID.`in`(this.entity.tasks.map { it.order.id } ))
                    .get()
    )

    /** Observable stop */
    val stop = stopQuery.result.map { it.value.first() }
            .behave(this)

    /** Stop orders */
    val orders = this.stop .map { it.tasks.map { it.order as OrderEntity } }
            .behave(this)

    /** Stop parcels */
    val parcels = this.stopParcelsQuery.result.map { it.value }
            .behave(this)

    val deliveredParcels = this.parcels.map { it.filter { it.deliveryState == Parcel.DeliveryState.DELIVERED } }
            .behave(this)

    val pendingParcels = this.parcels.map { it.filter { it.deliveryState == Parcel.DeliveryState.PENDING } }
            .behave(this)

    //region Counters
    val orderTotalAmount = this.orders.map { it.count() }
            .behave(this)

    val parcelTotalAmount = this.parcels.map { it.count() }
            .behave(this)

    val totalWeight = this.parcels.map { it.sumByDouble { it.weight } }
            .behave(this)

    val orderAmount = this.deliveredParcels.map { it.map { it.order }.distinct().count() }
            .behave(this)

    val parcelAmount = this.deliveredParcels.map { it.count() }
            .behave(this)

    val weight = this.deliveredParcels.map { it.sumByDouble { it.weight } }
            .behave(this)
    //endregion

}