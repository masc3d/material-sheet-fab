package org.deku.leo2.central;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 05.05.15.
 */
@Component
public class PersistenceContextAccessor {
    @PersistenceUnit
    public EntityManagerFactory mEntityManagerFactory;

    @PersistenceContext
    public EntityManager entityManager;
}
