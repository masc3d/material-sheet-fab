package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_NODE
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstNodeRecord
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR_ENTRY
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourEntryRecord
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourRecord
import org.deku.leoz.central.data.repository.fetchById
import org.deku.leoz.central.data.repository.fetchByUid
import org.deku.leoz.central.data.repository.uid
import org.deku.leoz.model.TaskType
import org.deku.leoz.service.internal.TourServiceV1
import org.jooq.DSLContext
import org.jooq.SelectWhereStep
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.zalando.problem.Status
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.DefaultProblem
import sx.time.toTimestamp
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Tour service implementation
 * Created by masc on 14.12.17.
 */
@Named
@Path("internal/v1/tour")
class TourServiceV1
    :
        org.deku.leoz.service.internal.TourServiceV1,
        MqHandler<TourServiceV1.TourUpdate> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var orderService: OrderService

    //region REST
    override fun getById(id: Int): TourServiceV1.Tour {
        val rTour = dsl.fetchOne(TAD_TOUR, TAD_TOUR.ID.eq(id))
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        val rNode = (dsl.selectFrom(MST_NODE).fetchById(rTour.nodeId)
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "No node uid for id [${rTour.nodeId}]")
                )

        return this.get(rTour, rNode)
    }

    override fun getByNode(nodeUid: String): TourServiceV1.Tour {
        val rNode = dsl.selectFrom(MST_NODE).fetchByUid(nodeUid)
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "Unknown node uid ${nodeUid}"
                )

        val rTour = dsl.fetchOne(TAD_TOUR, TAD_TOUR.NODE_ID.eq(rNode.nodeId))
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND
                )

        return this.get(rTour, rNode)
    }

    override fun getByUser(userId: Int): TourServiceV1.Tour {
        val rTour = dsl.fetchOne(
                TAD_TOUR,
                TAD_TOUR.USER_ID.eq(userId).and(TAD_TOUR.NODE_ID.isNull))
                ?:
                throw DefaultProblem(
                        status = Status.NOT_FOUND,
                        detail = "No assignable tour for user [${userId}]"
                )

        return this.get(rTour)
    }
    //endregion

    /**
     * Transform tour/node record into service result
     */
    private fun get(rTour: TadTourRecord, rNode: MstNodeRecord? = null): TourServiceV1.Tour {
        if (rTour.nodeId != null && rNode == null)
            throw IllegalArgumentException("Inconsistent arguments")

        if (rNode != null && rTour.nodeId != rNode.nodeId)
            throw IllegalArgumentException("Inconsistent arguments")

        // Fetch tour entries. Equal positions represent stop tasks in order of PK
        val rTourEntries = dsl
                .selectFrom(TAD_TOUR_ENTRY)
                .fetchByTourId(rTour.id)

        return TourServiceV1.Tour(
                id = rTour.id,
                nodeUid = rNode?.uid,
                userId = rTour.userId,
                orders = this.orderService.getByIds(
                        rTourEntries.map { it.orderId }.distinct()
                ),
                stops = rTourEntries.groupBy { it.position }
                        .map { stop ->
                            TourServiceV1.Stop(
                                    tasks = stop.value.map { task ->
                                        TourServiceV1.Task(
                                                orderId = task.orderId,
                                                taskType = when (TaskType.valueMap.getValue(task.orderTaskType)) {
                                                    TaskType.DELIVERY -> TourServiceV1.Task.Type.DELIVERY
                                                    TaskType.PICKUP -> TourServiceV1.Task.Type.PICKUP
                                                }
                                        )
                                    }
                            )
                        }
        )
    }

    /**
     * Tour update message handler
     */
    override fun onMessage(message: TourServiceV1.TourUpdate, replyChannel: MqChannel?) {
        log.trace { "Tour message ${message}" }

        val tour = message.tour ?: run {
            return
        }

        val nodeUid = tour.nodeUid ?: run {
            log.trace("Updates without node uid are not supported (yet)")
            return
        }

        val nodeId = dsl.selectFrom(MST_NODE).fetchByUid(nodeUid)
                ?.nodeId
                ?: run {
            log.warn("Node ${nodeUid} doesn't exist. Discarding message")
            return
        }

        // Upsert stop list
        dsl.transaction { _ ->
            // Check for existing tour
            val rTour = dsl.fetchOne(
                    TAD_TOUR,
                    TAD_TOUR.NODE_ID.eq(nodeId)
            ) ?:
                    // Create new one if it doesn't exist
                    dsl.newRecord(TAD_TOUR).also {
                        it.userId = tour.userId
                        it.nodeId = nodeId
                        it.timestamp = message.timestamp.toTimestamp()
                        it.store()
                    }

            // Recreate tour entries from update
            dsl
                    .delete(TAD_TOUR_ENTRY)
                    .where(TAD_TOUR_ENTRY.TOUR_ID.eq(rTour.id))
                    .execute()

            tour.stops.forEachIndexed { index, stop ->
                stop.tasks.forEach { task ->
                    dsl.newRecord(TAD_TOUR_ENTRY).also {
                        it.tourId = rTour.id
                        it.position = index.toDouble()
                        it.orderId = task.orderId
                        it.orderTaskType = when (task.taskType) {
                            TourServiceV1.Task.Type.DELIVERY -> TaskType.DELIVERY.value
                            TourServiceV1.Task.Type.PICKUP -> TaskType.DELIVERY.value
                        }
                        it.store()
                    }
                }
            }
        }
    }
}

/**
 * Fetch tour entries, correctly sorted by position/id
 * @param tourId Tour id
 */
fun SelectWhereStep<TadTourEntryRecord>.fetchByTourId(tourId: Int): List<TadTourEntryRecord> {
    // Fetch tour entries. Equal positions represent stop tasks in order of PK
    return this
            .where(TAD_TOUR_ENTRY.TOUR_ID.eq(tourId))
            .orderBy(TAD_TOUR_ENTRY.POSITION, TAD_TOUR_ENTRY.ID)
            .fetch()
}