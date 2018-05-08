package org.deku.leoz.node.data.repository

import org.deku.leoz.model.ContractType
import org.deku.leoz.node.data.jpa.MstStationContract
import org.deku.leoz.node.data.jpa.QMstStationContract.mstStationContract
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface StationContractRepository :
        JpaRepository<MstStationContract, Int>,
        QuerydslPredicateExecutor<MstStationContract>, StationContractRepositoryExtensions


interface StationContractRepositoryExtensions {
    fun findStationIds(debitorId: Int, contractType: ContractType = ContractType.DELIVERY): List<Int>
    fun findByStationId(stationId: Int, contractType: ContractType = ContractType.DELIVERY): MstStationContract?
}

class StationContractRepositoryImpl : StationContractRepositoryExtensions {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findStationIds(debitorId: Int, contractType: ContractType): List<Int> {
        return em.from(mstStationContract)
                .select(mstStationContract.stationId)
                .where(mstStationContract.debitorId.eq(debitorId)
                        .and(mstStationContract.contractType.eq(contractType.value)))
                .distinct()
                .fetch()
    }

    override fun findByStationId(stationId: Int, contractType: ContractType): MstStationContract? {
        return em.from(mstStationContract)
                .where(mstStationContract.stationId.eq(stationId)
                        .and(mstStationContract.contractType.eq(contractType.value)))
                .fetch()
                .lastOrNull()
    }
}