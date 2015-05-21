package org.deku.leo2.node.data.repositories;

import org.deku.leo2.node.data.entities.Station;
import org.deku.leo2.node.data.repositories.custom.StationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by masc on 30.04.15.
 */
public interface StationRepository extends JpaRepository<Station, Integer>, QueryDslPredicateExecutor, StationRepositoryCustom {

}
