package org.deku.leoz.mobile.service

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.service.internal.TourServiceV1
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.mqtt.channel
import java.util.*

/**
 * Mobile tour service
 * Created by masc on 19.01.18.
 */
class TourService : MqHandler<Any> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity by Kodein.global.lazy.instance()
    private val mqttEndpoints: MqttEndpoints by Kodein.global.lazy.instance()

    private val optimizedToursSubject = PublishSubject.create<TourServiceV1.TourOptimizationResult>()
    val optimizedTours = optimizedToursSubject.hide()

    @MqHandler.Types(
            TourServiceV1.TourOptimizationResult::class
    )
    override fun onMessage(message: Any, replyChannel: MqChannel?) {
        when (message) {
            is TourServiceV1.TourOptimizationResult -> this.onMessage(message)
        }
    }

    private fun onMessage(message: TourServiceV1.TourOptimizationResult) {
        this.optimizedToursSubject.onNext(message)
    }

    /**
     * Performs an observable tour optimimzation
     * @param omitAPpointments Omit appointments for optimization
     */
    fun optimize(
            omitAPpointments: Boolean = false
    ): Single<TourServiceV1.TourOptimizationResult> {
        // Unique request id for message based request
        val requestUid = UUID.randomUUID().toString()

        return this.optimizedTours
                // Ignore any response not referring to this request
                .filter { it.requestUid == requestUid }
                .firstOrError()
                .doOnSuccess { result ->
                    result.error?.also {
                        log.error("Optimization with request id [${result.requestUid}] failed [${it}]")
                    }
                }
                // Send optimization request when this observable is subscribed to
                .doOnSubscribe {
                    this.mqttEndpoints.central.main.channel().send(
                            TourServiceV1.TourOptimizationRequest(
                                    requestUid = requestUid,
                                    nodeUid = identity.uid.value,
                                    options = TourServiceV1.TourOptimizationOptions().also {
                                        it.appointments.omit = omitAPpointments
                                    }
                            ).also {
                                log.trace { "Sending optimization request [${it}]" }
                            }
                    )
                }
                .doOnSuccess {
                    log.trace { "Received optimization response [${it}]"}
                }
    }
}