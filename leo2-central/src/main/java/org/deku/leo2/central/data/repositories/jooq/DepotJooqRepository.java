package org.deku.leo2.central.data.repositories.jooq;

import org.deku.leo2.central.data.PersistenceConfiguration;
import org.deku.leo2.central.data.entities.jooq.Tables;
import org.deku.leo2.central.data.entities.jooq.tables.records.TbldepotlisteRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
@Named
public class DepotJooqRepository {
    @Inject
    DSLContext mDSLContext;

    @Transactional(PersistenceConfiguration.DB_CENTRAL)
    public List<TbldepotlisteRecord> findAll() {
        return mDSLContext.select()
                .from(Tables.TBLDEPOTLISTE)
                .fetchInto(TbldepotlisteRecord.class);
    }
}
