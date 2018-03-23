package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TaskType
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.QTadTourEntry.tadTourEntry
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.jpa.TadTourEntry
import org.deku.leoz.node.data.repository.StationRepository
import org.deku.leoz.node.data.repository.TadTourEntryRepository
import org.deku.leoz.node.data.repository.TadTourRepository
import org.deku.leoz.node.data.repository.toAddress
import org.deku.leoz.node.service.internal.SmartlaneBridge
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.TourServiceV1.*
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.transaction
import sx.persistence.withEntityManager
import sx.rs.RestProblem
import sx.rs.push
import sx.time.plusDays
import sx.time.toTimestamp
import sx.util.hashWithSha1
import sx.util.letWithParamNotNull
import sx.util.toNullable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.core.Response
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink
import kotlin.NoSuchElementException

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

    // Repositories
    @Inject
    private lateinit var deliverylistRepository: JooqDeliveryListRepository

    @Inject
    private lateinit var userService: UserService
    @Inject
    private lateinit var locationService: LocationServiceV2

    @Inject
    private lateinit var smartlane: SmartlaneBridge
    //endregion

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

                        val user = this.userService.getById(userId)

                        if (smartlane.hasDriver(user.email)) {
                            val positions = gpsMessage.dataPoints?.toList() ?: listOf()

                            this.smartlane.putDriverPosition(
                                    email = user.email,
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

    // TODO: move to delivery list service

    override fun create(deliverylistIds: List<Long>): List<Tour> {
        val dlRecords = deliverylistRepository
                .findByIds(deliverylistIds)

        dlRecords.map { it.id.toLong() }.let { deliverylistIds.subtract(it) }.also { missing ->
            if (missing.count() > 0)
                throw RestProblem(
                        status = Status.NOT_FOUND,
                        detail = "One or more delivery lists could not be found [${missing.joinToString(", ")}]")
        }

        val dlDetailRecordsByDlId = deliverylistRepository
                .findDetailsByIds(deliverylistIds)
                .groupBy { it.id }

        val tours =
                this.put(tours = dlRecords.map { dlRecord ->
                    Tour(
                            stationNo = dlRecord.deliveryStation.toLong(),
                            customId = dlRecord.id.toLong().toString(),
                            date = ShortDate(dlRecord.deliveryListDate),
                            stops = dlDetailRecordsByDlId.getValue(dlRecord.id)
                                    .sortedBy { it.orderPosition }
                                    .groupBy { it.orderPosition }
                                    .map { dlStop ->
                                        Stop(
                                                tasks = dlStop.value
                                                        .distinctBy { it.orderId.toString() + it.stoptype }
                                                        .map { dlDetailRecord ->
                                                            Task(
                                                                    orderId = dlDetailRecord.orderId.toLong(),
                                                                    taskType = when (TaskType.valueOf(dlDetailRecord.stoptype)) {
                                                                        TaskType.PICKUP -> Task.Type.PICKUP
                                                                        TaskType.DELIVERY -> Task.Type.DELIVERY
                                                                    }
                                                            )
                                                        }

                                        )

                                    }
                    )
                })

        return tours
    }
}