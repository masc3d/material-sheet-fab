package org.deku.leoz.mobile.model.entity.converter

import io.requery.Converter
import org.deku.leoz.model.ParcelService

/**
 * Requery service converter
 * Created by masc on 17.07.17.
 */
open class ServiceConverter : Converter<ArrayList<ParcelService>, String> {
    override fun getMappedType(): Class<ArrayList<ParcelService>> {
        @Suppress("UNCHECKED_CAST")
        return ArrayList::class.java as Class<ArrayList<ParcelService>>
    }

    override fun getPersistedType(): Class<String> {
        return String::class.java
    }

    override fun getPersistedSize(): Int? {
        return null
    }

    override fun convertToMapped(type: Class<out ArrayList<ParcelService>>?, value: String?): ArrayList<ParcelService> {
        return ArrayList((value ?: "")
                .split(',')
                .filter { it.isNotEmpty() }
                .map { ParcelService.byServiceId.get(it.toLong()) }
                .filterNotNull().toMutableList())
    }

    override fun convertToPersisted(value: ArrayList<ParcelService>?): String {
        return (value ?: ArrayList())
                .map { it.serviceId }
                .joinToString(",")
    }

}