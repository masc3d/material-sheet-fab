package org.deku.leoz.node.data.repositories.master;

import org.deku.leoz.node.data.entities.master.HolidayCtrl;
import org.deku.leoz.node.data.entities.master.HolidayCtrlPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 15.05.15.
 */
public interface HolidayctrlRepository extends JpaRepository<HolidayCtrl, HolidayCtrlPK>, QueryDslPredicateExecutor {
}
