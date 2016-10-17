package org.deku.leoz.central.data.repositories

import org.deku.leoz.central.data.entities.jooq.Tables
import org.deku.leoz.central.data.entities.jooq.tables.records.MstBundleVersionRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 27/02/16.
 */
@Named
class BundleVersionJooqRepository {
    @Inject
    lateinit var dslContext: DSLContext

    fun findByAlias(bundleName: String, versionAlias: String): MstBundleVersionRecord? {
        return dslContext.fetchOne(Tables.MST_BUNDLE_VERSION,
                Tables.MST_BUNDLE_VERSION.BUNDLE
                        .eq(bundleName)
                        .and(Tables.MST_BUNDLE_VERSION.ALIAS
                                .eq(versionAlias)))
    }
}