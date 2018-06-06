package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstBundleVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

/**
 * Bundle version repository
 * Created by masc on 11.05.15.
 */
interface BundleVersionRepository :
        JpaRepository<MstBundleVersion, String>,
        QuerydslPredicateExecutor<MstBundleVersion>
