package org.deku.leo2.node.data.repositories;

import org.deku.leo2.node.data.entities.Holidayctrl;
import org.deku.leo2.node.data.entities.HolidayctrlPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by JT on 15.05.15.
 */
public interface HolidayctrlRepository extends JpaRepository<Holidayctrl, HolidayctrlPK>, QueryDslPredicateExecutor
{
}
