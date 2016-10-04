package org.deku.leoz.node.data.repositories.master;

import org.deku.leoz.node.data.entities.MstSector;
import org.deku.leoz.node.data.entities.MstSectorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 16.05.15.
 */
public interface SectorRepository extends JpaRepository<MstSector, MstSectorId>, QueryDslPredicateExecutor<MstSector> {
}
