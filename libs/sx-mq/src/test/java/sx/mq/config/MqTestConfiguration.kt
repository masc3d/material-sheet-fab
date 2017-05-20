package sx.mq.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import org.apache.activemq.ActiveMQConnectionFactory
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import java.io.File
import javax.jms.ConnectionFactory

/**
 * Created by masc on 05.05.17.
 */
class MqTestConfiguration {
    companion object {
        val USERNAME = "mq"
        val PASSWORD = "mq"

        val module = Kodein.Module {
            bind<MqBroker>() with singleton {
                val broker = ActiveMQBroker.instance
                broker.user = MqBroker.User(
                        userName = USERNAME,
                        password = PASSWORD,
                        groupName = USERNAME)
                broker.dataDirectory = File("build/activemq")
                broker
            }

            bind<ConnectionFactory>() with singleton {
                ActiveMQPooledConnectionFactory(
                        ActiveMQBroker.instance.localUri,
                        USERNAME,
                        PASSWORD)
            }
        }
    }
}