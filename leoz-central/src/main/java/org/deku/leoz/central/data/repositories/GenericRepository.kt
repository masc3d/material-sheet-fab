package org.deku.leoz.central.data.repositories

import org.jooq.Cursor
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl

import javax.inject.Inject
import javax.inject.Named
import java.sql.Timestamp

/**
 * Generic central data access methods
 * Created by masc on 17.05.15.
 */
@Named
class GenericRepository {
    @Inject
    private lateinit var mDSLContext: DSLContext

    /**
     * Generic find newer function
     * @param ts Timestamp
     * *
     * @param table Jooq table
     * *
     * @param field Jooq field
     * *
     * @param  Type of jooq record
     * *
     * @return Jooq record
     */
    fun <TRecord : Record> findNewerThan(
            ts: Timestamp?,
            table: TableImpl<TRecord>,
            field: TableField<out Record, Timestamp>?): Cursor<TRecord> {

        return mDSLContext.selectFrom(table).where(if ((ts != null && field != null)) field.gt(ts) else DSL.trueCondition()).fetchLazy()
    }
}
