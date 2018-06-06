package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstBundleVersionRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.inject.Inject

/**
 * Created by masc on 27/02/16.
 */
@Component
class JooqBundleVersionRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dsl: DSLContext

    fun findByAlias(bundleName: String, versionAlias: String): MstBundleVersionRecord? {
        return dsl.fetchOne(Tables.MST_BUNDLE_VERSION,
                Tables.MST_BUNDLE_VERSION.BUNDLE
                        .eq(bundleName)
                        .and(Tables.MST_BUNDLE_VERSION.ALIAS
                                .eq(versionAlias)))
    }
}