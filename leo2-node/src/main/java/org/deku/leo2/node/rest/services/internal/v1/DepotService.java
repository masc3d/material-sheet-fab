package org.deku.leo2.node.rest.services.internal.v1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.data.entities.Station;
import org.deku.leo2.node.data.repositories.StationRepository;
import org.deku.leo2.node.rest.ApiKey;
import org.deku.leo2.rest.entities.internal.v1.Depot;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Created by masc on 17.09.14.
 */
@Named
@ApiKey(false)
@Path("internal/v1/depot")
@Produces(MediaType.APPLICATION_JSON)
public class DepotService implements org.deku.leo2.rest.services.internal.v1.DepotService {
    Log mLog = LogFactory.getLog(DepotService.class);

    @Inject
    StationRepository mStationRepository;

    /**
     * Convert to service result
     * @param d
     * @return
     */
    static Depot convert(Station d) {
        Depot rDepot = new Depot();
        rDepot.setDepotMatchcode(d.getuStId());
        rDepot.setDepotNr(d.getStationNr());
        rDepot.setFirma1(d.getBillingAddress1());
        rDepot.setFirma2(d.getBillingAddress2());
        rDepot.setLkz(d.getCountry());
        rDepot.setOrt(d.getCity());
        rDepot.setPlz(d.getZip());
        rDepot.setStrasse(d.getStreet());
        return rDepot;
    }

    @Override
    public Depot[] get() {
        // JPA/QueryDSL
        Iterable<Station> stations = mStationRepository.findAll();

        return StreamSupport.stream(stations.spliterator(), false)
                .map(s -> convert(s))
                .toArray(size -> new Depot[size]);
    }

    @Override
    public Depot[] find(String query) {
        List<Station> depots = mStationRepository.findWithQuery(query);

        return depots.stream()
                .map(s -> convert(s))
                .toArray(size -> new Depot[size]);
    }
}
