package org.deku.leo2.node.data;

import org.deku.leo2.node.DataTest;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 27.06.15.
 */
public class ThreadedTransactionTest extends DataTest {

    @PersistenceUnit(name = PersistenceConfiguration.DB_EMBEDDED)
    EntityManagerFactory mEntityManagerFactory;

    @Test
    public void testThreadedTransactions() {

    }
}
