package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.deku.leoz.central.data.jooq.tables.records.TblauftragcolliesRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.Persistence

/**
 * Created by JT on 18.07.17.
 */
@Named
class ParcelJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext

    fun saveEvent(eventRecord: TblstatusRecord): Boolean {
        return (eventRecord.store() > 0)
    }

    fun setSignaturePath(parcelNumber: String, path: String): Boolean {
        //update parcel
        //TblauftragcolliesRecord: bmpFileName = path
        return true
    }
}