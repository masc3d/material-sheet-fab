package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstBundleVersionRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by masc on 27/02/16.
 */
@Named
class JooqBundleVersionRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun findByAlias(bundleName: String, versionAlias: String): MstBundleVersionRecord? {
        return dslContext.fetchOne(Tables.MST_BUNDLE_VERSION,
                Tables.MST_BUNDLE_VERSION.BUNDLE
                        .eq(bundleName)
                        .and(Tables.MST_BUNDLE_VERSION.ALIAS
                                .eq(versionAlias)))
    }
}