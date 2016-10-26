package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.jpa.MstHolidayCtrl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by JT on 15.05.15.
 */
interface HolidayCtrlRepository :
        JpaRepository<MstHolidayCtrl, Long>,
        QueryDslPredicateExecutor<MstHolidayCtrl>
