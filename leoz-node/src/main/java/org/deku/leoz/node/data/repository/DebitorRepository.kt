package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstDebitor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface DebitorRepository:
        JpaRepository<MstDebitor, Long>,
        QuerydslPredicateExecutor<MstDebitor>
