package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.jooq.Cursor
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.springframework.beans.factory.annotation.Qualifier
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Named

/**
 * Generic central data access methods
 * Created by masc on 17.05.15.
 */
@Named
class GenericJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    /**
     * Generic find newer function
     * @param syncId Optional sync id. If omitted all records are fetched.
     * @param table Jooq table
     * @param field Jooq field
     * @return Jooq record
     */
    fun <TRecord : Record> findNewerThan(
            syncId: Long?,
            table: TableImpl<TRecord>,
            field: TableField<out Record, Long>?): Cursor<TRecord> {

        return dslContext.selectFrom(table)
                .where(if ((syncId != null && field != null)) field.gt(syncId) else DSL.trueCondition())
                .fetchLazy()
    }
}
