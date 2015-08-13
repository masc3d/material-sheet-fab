package org.deku.leoz.node.data;

import org.deku.leoz.node.DataTest;
import org.deku.leoz.node.data.entities.master.Station;
import org.deku.leoz.node.data.repositories.master.StationRepository;
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
