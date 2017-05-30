package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by helke on 29.05.17.
 */
@Named
class PositionJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext
}
