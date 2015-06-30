package org.deku.leo2.node.data;

import org.deku.leo2.node.DataTest;
import org.deku.leo2.node.data.entities.master.Station;
import org.deku.leo2.node.data.repositories.master.StationRepository;
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
