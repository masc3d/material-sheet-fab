package org.deku.leo2.central.data;

import org.deku.leo2.central.data.entities.Depot;
import org.deku.leo2.central.data.repositories.DepotRepository;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by masc on 15.05.15.
 */
public class DepotRepositoryTest extends DataTest {
    @Inject
    DepotRepository mDepotRepository;

    @Test
    public void test() {
        for (Depot d : mDepotRepository.findAll()) {
            System.out.println(d);
        }
    }
}
