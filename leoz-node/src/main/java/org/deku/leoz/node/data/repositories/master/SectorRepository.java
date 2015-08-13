package org.deku.leoz.node.data.repositories.master;

import org.deku.leoz.node.data.entities.master.Sector;
import org.deku.leoz.node.data.entities.master.SectorPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 16.05.15.
 */
public interface SectorRepository extends JpaRepository<Sector, SectorPK>, QueryDslPredicateExecutor {
}
