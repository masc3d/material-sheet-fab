package org.deku.leoz.central.service.internal

import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.node.data.repository.StationContractRepository
import org.deku.leoz.node.data.repository.TadTourRepository
import org.deku.leoz.node.service.smartlane.SmartlaneBridge
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.mq.MqHandler
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.ws.rs.Path

/**
 * Tour service implementation
 * Created by masc on 14.12.17.
 */
@Component
@Path("internal/v1/tour")
class TourServiceV1
    :
        org.deku.leoz.node.service.internal.TourServiceV1(),
        MqHandler<Any> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var locationService: LocationServiceV2

    @Inject
    private lateinit var tourRepo: TadTourRepository

    @Inject
    private lateinit var smartlane: SmartlaneBridge

    // TODO: move to node as soon as location service has been migrated

    @PostConstruct
    override fun onInitialize() {
        // Location received event
        this.locationService.locationReceived
                .subscribe { gpsMessage ->
                    try {
                        val userId = gpsMessage.userId ?: run {
                            // Skip location updates without user id
                            return@subscribe
                        }

                        // Most recent tour
                        val tour = tourRepo.findMostRecentByUserId(userId.toLong())

                        if (tour == null || tour.stationNo == null) {
                            // No recent tour or legacy tour without station no -> silently ignoring
                            return@subscribe
                        }

                        val user = this.userService.getById(userId)

                        if (smartlane.hasDriver(user)) {
                            val positions = gpsMessage.dataPoints?.toList() ?: listOf()

                            this.smartlane.putDriverPosition(
                                    user = user,
                                    positions = positions
                            )
                                    .subscribeBy(
                                            onError = { e -> log.error(e.message, e) }
                                    )
                        }
                    } catch (e: Throwable) {
                        log.error("Location push to routing provider failed. node [${gpsMessage.nodeKey}], user [${gpsMessage.userId}]: ${e.message}")
                    }
                }
    }
}