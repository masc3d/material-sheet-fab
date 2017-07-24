package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.OrderEntity
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import kotlin.reflect.KClass

/**
 * Abstract observing repository
 * Created by masc on 23.07.17.
 */
abstract class ObservingRepository<T : Persistable>(
        private val entityType: KClass<T>,
        private val store: KotlinReactiveEntityStore<Persistable>) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val entitiesProperty = ObservableRxProperty(listOf<T>())
    val entities by entitiesProperty

    /**
     * Self observable entity query
     */
    private val _entities = store.select(this.entityType)
            .get()
            .observableResult()
            .subscribeBy(
                    onNext = {
                        val entities = it.toList()
                        log.trace("ENTITIES CHANGED ${this.entityType.java.simpleName}, amount ${entities.count()}")
                        this.entitiesProperty.set(entities)

                        entities.forEach {
                            if (it is Observable) {
                                it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                                    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                                        log.trace("ENTITY ${this@ObservingRepository.entityType.java.simpleName}, FIELD ${propertyId}")
                                    }
                                })
                            }
                        }
                    },
                    onError = {
                        log.error(it.message, it)
                    }
            )
}