package org.deku.leo2.central.data.repositories.jooq;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;

/**
 * Created by masc on 17.05.15.
 */
@Named
public class GenericJooqRepository {
    @Inject
    DSLContext mDSLContext;

    /**
     * Generic find newer function
     * @param ts Timestamp
     * @param table Jooq table
     * @param field Jooq field
     * @param <TRecord> Type of jooq record
     * @return Jooq record
     */
    public <TRecord extends Record> Cursor<TRecord> findNewerThan(
            Timestamp ts,
            TableImpl<TRecord> table,
            TableField<? extends Record, Timestamp> field) {

        return mDSLContext.selectFrom(table)
                .where((ts != null && field != null) ? field.gt(ts) : DSL.trueCondition())
                .fetchLazy();
    }
}
