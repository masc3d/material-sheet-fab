package org.deku.leoz.node.data.repositories.system

import org.deku.leoz.node.data.entities.SysProperty
import org.deku.leoz.node.data.entities.SysPropertyId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor


/**
 * Created by JT on 29.06.15.
 */
interface PropertyRepository :
        JpaRepository<SysProperty, SysPropertyId>,
        QueryDslPredicateExecutor<SysProperty>
