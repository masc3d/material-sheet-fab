package org.deku.leoz.node.data.repository.master

import org.deku.leoz.node.data.jpa.MstHolidayCtrl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Holiday repository
 * Created by masc on 15.05.15.
 */
interface HolidayCtrlRepository :
        JpaRepository<MstHolidayCtrl, Long>,
        QueryDslPredicateExecutor<MstHolidayCtrl>
