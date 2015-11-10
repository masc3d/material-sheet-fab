package org.deku.leoz.central.data.repositories;

import org.deku.leoz.central.config.PersistenceConfiguration;
import org.deku.leoz.central.data.entities.jooq.Tables;
import org.deku.leoz.central.data.entities.jooq.tables.records.TbldepotlisteRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
@Named
public class DepotRepository {
    @Inject
    DSLContext mDSLContext;

    @Transactional(PersistenceConfiguration.QUALIFIER)
    public List<TbldepotlisteRecord> findAll() {
        return mDSLContext.select()
                .from(Tables.TBLDEPOTLISTE)
                .fetchInto(TbldepotlisteRecord.class);
    }
}
