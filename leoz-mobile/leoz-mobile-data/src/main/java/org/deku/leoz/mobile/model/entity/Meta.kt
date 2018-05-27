package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.converter.JsonConverter
import sx.android.databinding.BaseRxObservable
import sx.io.serialization.Serializer
import kotlin.reflect.KClass

/**
 * Mobile meta data entity baseclass.
 *
 * Supports storing serializable objects as relational metadata.
 * Also stores the type uid as a separate field for fast lookups.
 *
 * Suitable for adiitional opaque records/metadata which do not have ot be queried relationally.
 *
 * Created by masc on 18.07.17.
 */
@Superclass
abstract class Meta : BaseRxObservable(), Persistable, Observable {
    @get:Key
    @get:Generated
    abstract val id: Long

    /** Meta info type, represented as @Serializable uid */
    @get:Column(name = "type_uid", nullable = false)
    abstract var typeUid: Long
        protected set

    /** Meta info value */
    @get:Lazy
    @get:Column(name = "value_", nullable = false)
    @get:Convert(JsonConverter::class)
    abstract var value: Any
        protected set

    fun set(value: Any) {
        typeUid = Serializer.types.typeOf(value.javaClass).uid
        this.value = value
    }
}

// Extension methods for metadata collections

/**
 * Filter metas by type
 */
fun <T : Meta> List<T>.filter(cls: KClass<*>): List<T> {
    val uid = Serializer.types.typeOrNullOf(cls.java)?.uid

    if (uid == null)
        return listOf()

    return this.filter { it.typeUid == uid }
}

/**
 * First meta by type
 */
fun <T : Meta> List<T>.firstOrNull(cls: KClass<*>): T? {
    val uid = Serializer.types.typeOrNullOf(cls.java)?.uid

    if (uid == null)
        return null

    return this.filter { it.typeUid == uid }.firstOrNull()
}

/**
 * Meta values by type
 */
fun <T: Meta, R : Any> List<T>.values(cls: KClass<R>): List<R> {
    return this.filter(cls).mapNotNull {
        @Suppress("UNCHECKED_CAST")
        it.value as? R
    }
}

/**
 * Meta value by type
 */
fun <T : Meta, R : Any> List<T>.valueOrNull(cls: KClass<R>): R? {
    @Suppress("UNCHECKED_CAST")
    return this.firstOrNull(cls)?.value as? R?
}

/**
 * Set meta of specific type, replacing all existing meta values of this type
 */
fun <T : Meta> MutableList<T>.set(meta: T) {
    this.removeAll { it.typeUid == meta.typeUid }
    this.add(meta)
}