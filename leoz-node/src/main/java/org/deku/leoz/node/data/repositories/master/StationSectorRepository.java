package org.deku.leoz.node.data.repositories.master;

import org.deku.leoz.node.data.entities.master.StationSector;
import org.deku.leoz.node.data.entities.master.StationSectorPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 29.06.15.
 */
public interface StationSectorRepository extends JpaRepository<StationSector,StationSectorPK>, QueryDslPredicateExecutor<StationSector> {
}
