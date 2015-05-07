package org.deku.leo2.central;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 05.05.15.
 */
@Named
public class PersistenceContextAccessor {
    @PersistenceUnit
    public EntityManagerFactory mEntityManagerFactory;

    @PersistenceContext
    public EntityManager entityManager;
}
