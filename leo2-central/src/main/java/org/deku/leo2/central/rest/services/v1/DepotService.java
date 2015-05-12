package org.deku.leo2.central.rest.services.v1;

import org.deku.leo2.central.data.entities.Depot;
import org.deku.leo2.central.data.entities.QDepot;
import org.deku.leo2.central.data.entities.jooq.tables.records.TbldepotlisteRecord;
import org.deku.leo2.central.data.repositories.DepotJooqRepository;
import org.deku.leo2.central.data.repositories.DepotRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Component
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
public class DepotService implements org.deku.leo2.rest.services.v1.DepotService {
    Logger mLog = Logger.getLogger(DepotService.class.getName());

    @Inject
    DepotJooqRepository mDepotJooqRepository;

    @Inject
    DepotRepository mDepotRepository;

    /**
     * Convert to service result
     * @param d
     * @return
     */
    static org.deku.leo2.rest.entities.v1.Depot convert(Depot d) {
        org.deku.leo2.rest.entities.v1.Depot rDepot = new org.deku.leo2.rest.entities.v1.Depot();
        rDepot.setDepotMatchcode(d.getDepotMatchcode());
        rDepot.setDepotNr(d.getDepotNr());
        rDepot.setFirma1(d.getFirma1());
        rDepot.setFirma2(d.getFirma2());
        rDepot.setLkz(d.getLkz());
        rDepot.setOrt(d.getOrt());
        rDepot.setPlz(d.getPlz());
        rDepot.setStrasse(d.getStrasse());
        return rDepot;
    }

    /**
     * Convert jooq depot record to service result
     * @param d
     * @return
     */
    static org.deku.leo2.rest.entities.v1.Depot convert(TbldepotlisteRecord d) {
        org.deku.leo2.rest.entities.v1.Depot rDepot = new org.deku.leo2.rest.entities.v1.Depot();
        rDepot.setDepotMatchcode(d.getDepotmatchcode());
        rDepot.setDepotNr(d.getDepotnr());
        rDepot.setFirma1(d.getFirma1());
        rDepot.setFirma2(d.getFirma2());
        rDepot.setLkz(d.getLkz());
        rDepot.setOrt(d.getOrt());
        rDepot.setPlz(d.getPlz());
        rDepot.setStrasse(d.getStrasse());
        return rDepot;
    }

    @Transactional("jooq")
    @Override
    public org.deku.leo2.rest.entities.v1.Depot[] get() {
        // JPA/QueryDSL
//        Iterable<Depot> depots = mDepotRepository.findAll();
//
//        return StreamSupport.stream(depots.spliterator(), false)
//                .map(d -> convert(d))
//                .toArray(size -> new org.deku.leo2.rest.entities.v1.Depot[size]);

        // JOOQ
        List<TbldepotlisteRecord> depots = mDepotJooqRepository.findAll();

        return depots.stream().map(d -> convert(d))
                .toArray(size -> new org.deku.leo2.rest.entities.v1.Depot[size]);
    }

    public void test() {
        QDepot q = null;
    }

    @Transactional("jpa")
    @Override
    public org.deku.leo2.rest.entities.v1.Depot[] find(String query) {
        List<Depot> depots = mDepotRepository.findWithQuery(query);

        return depots.stream().map(d -> convert(d))
                .toArray(size -> new org.deku.leo2.rest.entities.v1.Depot[size]);
    }
}
