package org.deku.leo2.central.data;

import org.deku.leo2.central.DataTest;
import org.deku.leo2.node.data.entities.Station;
import org.deku.leo2.node.data.repositories.StationRepository;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by masc on 15.05.15.
 */
public class DepotRepositoryTest extends DataTest {
    @Inject
    StationRepository mStationRepository;

    @Test
    public void test() {
        for (Station d : mStationRepository.findAll()) {
            System.out.println(d);
        }
    }
}
