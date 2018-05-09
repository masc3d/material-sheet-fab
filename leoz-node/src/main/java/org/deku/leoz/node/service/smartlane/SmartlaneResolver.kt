package org.deku.leoz.node.service.smartlane

import org.deku.leoz.model.ContractType
import org.deku.leoz.node.data.jpa.MstStationContract
import org.deku.leoz.node.data.repository.StationContractRepository
import org.deku.leoz.node.data.repository.TadTourRepository
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.UserService
import org.slf4j.LoggerFactory
import sx.log.slf4j.warn
import javax.inject.Inject
import javax.inject.Named

/**
 * Leoz smartlane container resolver
 * Created by masc on 08.05.18.
 */
@Named
class SmartlaneResolver : SmartlaneBridge.Resolver {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var stationContractRepo: StationContractRepository

    @Inject
    private lateinit var tourRepo: TadTourRepository

    /** Build smartlane container url path from contract */
    private fun MstStationContract.smartlaneUrlPath(): String {
        val contractNo = this.contractNo

        if (contractNo.isNullOrEmpty())
            throw IllegalArgumentException("No contract for station id [${this.stationId}]")

        return when {
            contractNo.all { it.isDigit() } ->
                // All digit contract no -> url path by convention
                "deku-${this.debitorId}-${this.contractNo.trim()}"
            else ->
                // Support for custom (alphanumeric) container paths (useful for test)
                contractNo
        }
    }

    /**
     * Resolve container by station no
     * @param stationNo station no
     */
    private fun containerByStationNo(stationNo: Int): SmartlaneBridge.Container {
        val contract = stationContractRepo
                .findByStationNo(
                        stationNo = stationNo.toInt(),
                        contractType = ContractType.SMARTLANE
                )
                ?: throw NoSuchElementException("No smartlane contract for station no [${stationNo}]")

        return SmartlaneBridge.Container(
                path = contract.smartlaneUrlPath()
        )
    }

    /**
     * Resolve container by tour
     * @param tour tour
     */
    override fun containerByTour(tour: TourServiceV1.Tour): SmartlaneBridge.Container {
        val stationNo = tour.stationNo
                ?: throw NoSuchElementException("Station no [${tour.stationNo}] of tour [${tour.uid}] doesn't exist")

        return this.containerByStationNo(stationNo.toInt())
    }

    /**
     * Resolve container by user
     * @param user user
     */
    override fun containerByUser(user: UserService.User): SmartlaneBridge.Container {
        val userId = user.id
                ?: throw IllegalArgumentException("User is missing id")

        val tour = tourRepo.findMostRecentByUserId(userId.toLong())
                ?: throw NoSuchElementException("No recent tour for user id [${user}]|")

        val stationNo = tour.stationNo
                ?: throw IllegalArgumentException("Tour [${tour.uid}] is missing station no")

        return this.containerByStationNo(stationNo.toInt())
    }
}

