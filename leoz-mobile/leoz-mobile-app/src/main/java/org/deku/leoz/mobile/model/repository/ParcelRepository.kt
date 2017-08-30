package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.mq.MimeType
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.mq.sendFile
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.mq.mqtt.channel
import sx.rx.ObservableRxProperty
import java.util.*

/**
 * Parcel repository
 * Created by masc on 20.07.17.
 */
class ParcelRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) : ObservingRepository<ParcelEntity>(ParcelEntity::class, store) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val mqttEndPoints: MqttEndpoints by Kodein.global.lazy.instance()

    /**
     * Find parcel by unit number
     * @param number Unit number
     */
    fun findByNumber(number: String): Parcel? {
        return store.select(ParcelEntity::class)
                .where(ParcelEntity.NUMBER.eq(number))
                .get()
                .firstOrNull()
    }

    override fun update(entity: ParcelEntity): Single<ParcelEntity> {
        entity.modificationTime = Date()
        return super.update(entity)
    }

    /**
     * Mark parcel as damaged
     * @param parcel Parcel to mark damaged
     * @param jpegPictureData Damaged parcel picture
     */
    fun markDamaged(parcel: ParcelEntity, jpegPictureData: ByteArray): Completable {
        return Single.fromCallable {
            // Send file
            mqttEndPoints.central.main.channel().sendFile(jpegPictureData, MimeType.JPEG.value)
        }.map { fileUid ->
            // Update parcel with damaged info
            parcel.isDamaged = true

            parcel.meta.add(ParcelMeta.create(
                    Parcel.DamagedInfo(pictureFileUid = fileUid)
            ))

            this.update(parcel)
                    .blockingGet()
        }.toCompletable()
    }
}
