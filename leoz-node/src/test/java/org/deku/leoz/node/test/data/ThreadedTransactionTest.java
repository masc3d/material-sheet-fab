package org.deku.leoz.node.test.data;

import org.deku.leoz.node.test.DataTest;
import org.deku.leoz.node.config.PersistenceConfiguration;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 27.06.15.
 */
public class ThreadedTransactionTest extends DataTest {

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    EntityManagerFactory mEntityManagerFactory;

    @Test
    public void testThreadedTransactions() {

    }
}
