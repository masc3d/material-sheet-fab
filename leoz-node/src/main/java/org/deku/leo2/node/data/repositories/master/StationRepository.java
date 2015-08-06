package org.deku.leo2.node.data.repositories.master;

import org.deku.leo2.node.data.entities.master.Station;
import org.deku.leo2.node.data.repositories.master.custom.StationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 30.04.15.
 */
public interface StationRepository extends JpaRepository<Station, Integer>, QueryDslPredicateExecutor, StationRepositoryCustom {
}
