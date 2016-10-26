package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstBundleVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by JT on 11.05.15.
 */
interface BundleVersionRepository :
        JpaRepository<MstBundleVersion, String>,
        QueryDslPredicateExecutor<MstBundleVersion>
