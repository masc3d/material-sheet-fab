package org.deku.leoz.mobile.service

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.Diagnostics
import org.deku.leoz.service.internal.NodeServiceV1
import sx.mq.MqChannel
import sx.mq.MqHandler

/**
 * Mobile node service
 * Created by masc on 19.12.17.
 */
class NodeService
    :
        MqHandler<NodeServiceV1.DiagnosticDataRequest> {

    private val diagnostics: Diagnostics by Kodein.global.lazy.instance()

    /**
     * Diagnostic data request handler
     */
    override fun onMessage(message: NodeServiceV1.DiagnosticDataRequest, replyChannel: MqChannel?) {
        diagnostics.send()
    }
}