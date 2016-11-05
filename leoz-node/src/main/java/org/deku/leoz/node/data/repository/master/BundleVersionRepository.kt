package org.deku.leoz.node.data.repository.master

import org.deku.leoz.node.data.jpa.MstBundleVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Bundle version repository
 * Created by masc on 11.05.15.
 */
interface BundleVersionRepository :
        JpaRepository<MstBundleVersion, String>,
        QueryDslPredicateExecutor<MstBundleVersion>
