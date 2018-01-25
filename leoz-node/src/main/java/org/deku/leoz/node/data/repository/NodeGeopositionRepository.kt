package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.TadNodeGeoposition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface NodeGeopositionRepository:
        JpaRepository<TadNodeGeoposition,Long>,
        QuerydslPredicateExecutor<TadNodeGeoposition>