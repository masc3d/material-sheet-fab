package org.deku.leo2.central.rest.services.internal.v1;

import org.deku.leo2.central.data.entities.Station;
import org.deku.leo2.central.data.entities.jooq.tables.records.TbldepotlisteRecord;
import org.deku.leo2.central.data.repositories.StationRepository;
import org.deku.leo2.central.data.repositories.jooq.DepotJooqRepository;
import org.deku.leo2.rest.entities.internal.v1.Depot;
import org.springframework.stereotype.Component;

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
@Path("internal/v1/depot")
@Produces(MediaType.APPLICATION_JSON)
public class DepotService implements org.deku.leo2.rest.services.internal.v1.DepotService {
    Logger mLog = Logger.getLogger(DepotService.class.getName());

    @Inject
    DepotJooqRepository mDepotJooqRepository;

    @Inject
    StationRepository mStationRepository;

    /**
     * Convert to service result
     * @param d
     * @return
     */
    static Depot convert(Station d) {
        Depot rDepot = new Depot();
//        rDepot.setDepotMatchcode(d.getDepotMatchcode());
//        rDepot.setDepotNr(d.getStationNr());
//        rDepot.setFirma1(d.getFirma1());
//        rDepot.setFirma2(d.getFirma2());
//        rDepot.setLkz(d.getLkz());
//        rDepot.setOrt(d.getOrt());
//        rDepot.setPlz(d.getPlz());
//        rDepot.setStrasse(d.getStrasse());
        return rDepot;
    }

    /**
     * Convert jooq depot record to service result
     * @param d
     * @return
     */
    static Depot convert(TbldepotlisteRecord d) {
        Depot rDepot = new Depot();
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

    @Override
    public Depot[] get() {
        // JPA/QueryDSL
//        Iterable<Depot> depots = mStationRepository.findAll();
//
//        return StreamSupport.stream(depots.spliterator(), false)
//                .map(d -> convert(d))
//                .toArray(size -> new org.deku.leo2.rest.entities.internal.v1.Depot[size]);

        // JOOQ
        List<TbldepotlisteRecord> depots = mDepotJooqRepository.findAll();

        return depots.stream().map(d -> convert(d))
                .toArray(size -> new Depot[size]);
    }

    @Override
    public Depot[] find(String query) {
        List<Station> depots = mStationRepository.findWithQuery(query);

        return depots.stream().map(d -> convert(d))
                .toArray(size -> new Depot[size]);
    }
}
