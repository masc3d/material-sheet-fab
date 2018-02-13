package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.LclSync
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

/**
 * Sync repository
 * Created by JT on 29.06.15.
 */
interface SyncRepository :
        JpaRepository<LclSync, Long>,
        QuerydslPredicateExecutor<LclSync>
