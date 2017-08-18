package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.converter.JsonConverter
import sx.android.databinding.BaseRxObservable
import sx.io.serialization.Serializer

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

fun <T : Meta> List<T>.findByType(cls: Class<*>): T? {
    val uid = Serializer.types.typeOrNullOf(cls)?.uid

    if (uid == null)
        return null

    return this.first { it.typeUid == uid }
}

fun <T : Meta, R> List<T>.findValueByType(cls: Class<R>): R? {
    @Suppress("UNCHECKED_CAST")
    return this.findByType(cls)?.value as? R?
}