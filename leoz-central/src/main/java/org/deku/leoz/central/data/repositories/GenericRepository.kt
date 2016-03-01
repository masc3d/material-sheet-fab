package org.deku.leoz.central.data.repositories

import org.jooq.Cursor
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Named

/**
 * Generic central data access methods
 * Created by masc on 17.05.15.
 */
@Named
class GenericRepository {
    @Inject
    private lateinit var dslContext: DSLContext

    /**
     * Generic find newer function
     * @param timestamp Optional timestamp. If omitted all records are fetched.
     * @param table Jooq table
     * @param field Jooq field
     * @return Jooq record
     */
    fun <TRecord : Record> findNewerThan(
            timestamp: Timestamp?,
            table: TableImpl<TRecord>,
            field: TableField<out Record, Timestamp>?): Cursor<TRecord> {

        return dslContext.selectFrom(table)
                .where(if ((timestamp != null && field != null)) field.gt(timestamp) else DSL.trueCondition())
                .fetchLazy()
    }
}
