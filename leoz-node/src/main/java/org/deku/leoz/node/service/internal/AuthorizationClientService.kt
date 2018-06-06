package org.deku.leoz.node.service.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.identity.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.service.internal.NodeServiceV1
import org.threeten.bp.Duration
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.rx.ObservableRxProperty
import javax.inject.Named

/**
 * Authorization client service, performing background authorization via message bus
 * Created by masc on 01.07.15.
 */
class AuthorizationClientService(
        executorService: java.util.concurrent.ScheduledExecutorService,
        private val identitySupplier: () -> Identity,
        private val onRejected: (identity: Identity) -> Unit)
    :
        sx.concurrent.Service(executorService,
                period = Duration.ofSeconds(60)),

        // Message handler for retrieving push authorization updates
        MqHandler<AuthorizationService.NodeResponse> {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity
        get() = identitySupplier()

    private val objectMapper = ObjectMapper()

    private val application: Application by Kodein.global.lazy.instance()

    private val systemInformation: SystemInformation by Kodein.global.lazy.instance()

    private val storage: Storage by Kodein.global.lazy.instance()

    val isAuthorizedProperty = ObservableRxProperty(false)
    /** Indicates node authorization state */
    var isAuthorized by isAuthorizedProperty

    override fun run() {
        // Send info message
        JmsEndpoints.central.main.kryo.channel().use {
            it.send(
                    NodeServiceV1.Info(
                            uid = identity.uid.value,
                            bundleName = this.application.name,
                            bundleVersion = this.application.version,
                            hardwareSerialNumber = null,
                            systemInformation = this.objectMapper.writeValueAsString(this.systemInformation)
                    ).also {
                        log.info("Sending ${it}")
                    }
            )
        }

        // Authorization message exchange
        val authorizationMessage = JmsEndpoints.central.main.kryo.channel().use {
            it.sendRequest(
                    AuthorizationService.NodeRequest(
                            name = identity.name,
                            key = identity.uid.value
                    ).also {
                        log.info("Sending ${it}")
                    }
            ).use {
                it.receive(AuthorizationService.NodeResponse::class.java)
            }
        }

        // Set id based on response and store identity
        log.info("Received authorization [%s]".format(authorizationMessage))
        if (authorizationMessage.rejected) {
            this.onRejected(this.identity)
            this.isAuthorized = false
        } else {
            this.identity.save(storage.identityConfigurationFile)
            this.isAuthorized = true
        }

        // Stop service when authorization process completed
        this.stop(async = true)
    }

    override fun onMessage(message: AuthorizationService.NodeResponse, replyChannel: MqChannel?) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
