package org.deku.leoz.node.data.repositories.master

import org.deku.leoz.node.data.entities.MstHolidayCtrl
import org.deku.leoz.node.data.entities.MstHolidayCtrlId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by JT on 15.05.15.
 */
interface HolidayCtrlRepository :
        JpaRepository<MstHolidayCtrl, MstHolidayCtrlId>,
        QueryDslPredicateExecutor<MstHolidayCtrl>
