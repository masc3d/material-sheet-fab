package org.deku.leo2.node.data.repositories;

import org.deku.leo2.node.data.entities.Sector;
import org.deku.leo2.node.data.entities.SectorPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 16.05.15.
 */
public interface SectorRepository extends JpaRepository<Sector, SectorPK>, QueryDslPredicateExecutor {
}
