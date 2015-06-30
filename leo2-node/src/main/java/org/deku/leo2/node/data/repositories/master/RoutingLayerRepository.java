package org.deku.leo2.node.data.repositories.master;

import org.deku.leo2.node.data.entities.master.RoutingLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 23.06.15.
 */
public interface RoutingLayerRepository extends JpaRepository<RoutingLayer,Integer>,QueryDslPredicateExecutor
{
}


