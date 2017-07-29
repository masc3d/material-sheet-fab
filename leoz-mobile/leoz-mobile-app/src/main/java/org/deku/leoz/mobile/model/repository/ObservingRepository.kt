package org.deku.leoz.mobile.model.repository

import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.slf4j.LoggerFactory
import sx.requery.ObservableQuery
import kotlin.reflect.KClass

/**
 * Abstract observing repository
 * Created by masc on 23.07.17.
 */
abstract class ObservingRepository<E : Persistable>(
        private val entityType: KClass<E>,
        private val store: KotlinReactiveEntityStore<Persistable>) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val query = ObservableQuery<E>(
            store.select(this.entityType).get()
    )

    val entitiesProperty = query.result
    val entities by entitiesProperty

    open fun update(entity: E): Single<E> {
        return store.update(entity)
    }
}