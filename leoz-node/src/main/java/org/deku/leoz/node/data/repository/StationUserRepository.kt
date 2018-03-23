package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstStationUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor


interface StationUserRepository:
        JpaRepository<MstStationUser,Long>,
        QuerydslPredicateExecutor<MstStationUser>
