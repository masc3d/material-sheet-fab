package org.deku.leoz.node.data.repositories.master;

import org.deku.leoz.node.data.entities.MstRoutingLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 23.06.15.
 */
public interface RoutingLayerRepository extends JpaRepository<MstRoutingLayer,Integer>, QueryDslPredicateExecutor<MstRoutingLayer>
{
}


