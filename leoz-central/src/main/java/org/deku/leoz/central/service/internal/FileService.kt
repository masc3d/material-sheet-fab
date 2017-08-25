package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.FileServiceV1
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
import javax.inject.Named
import javax.ws.rs.Path

/**
 * File service V1 implementation
 * Created by masc on 25.08.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/file")
class FileServiceV1 :
        FileServiceV1,
        MqHandler<FileServiceV1.FileFragmentMessage> {

    override fun onMessage(message:FileServiceV1.FileFragmentMessage, replyChannel: MqChannel?) {
    }
}