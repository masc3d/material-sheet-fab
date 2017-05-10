package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.singleton
import sx.mq.Broker
import sx.mq.jms.activemq.ActiveMQBroker
import java.io.File

/**
 * Created by masc on 05.05.17.
 */
class MessagingTestConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<ActiveMQBroker>() with singleton {
                val broker = ActiveMQConfiguration.broker
                broker.user = Broker.User(
                        userName = MqConfiguration.USERNAME,
                        password = MqConfiguration.PASSWORD,
                        groupName = MqConfiguration.GROUPNAME)
                broker.dataDirectory = File("build/activemq")
                broker
            }
        }
    }
}