package org.deku.leoz.mobile.config

import android.content.Context
import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.model.RemoteSettings
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import sx.android.Connectivity
import sx.android.mqtt.MqttSqlitePersistence
import sx.mq.mqtt.*
import java.util.concurrent.ExecutorService


/**
 * Mobile MQTT configuration
 * Created by masc on 10.05.17.
 */
class MqttConfiguration(
        context: MqttContext
) : org.deku.leoz.config.MqttConfiguration(context) {

    /**
     * Listener for mobile topic channel
     */
    val mobileTopicListener: MqttListener by lazy {
        MqttListener(
                mqttChannel = this.mobileTopic)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MqttConfiguration::class.java)

        var sub: Disposable? = null

        val module = Module {
            /**
             * MQTT connection options
             */
            bind<MqttConnectOptions>() with singleton {
                val mqttConnectOptions = MqttConnectOptions()
                // Disabling paho's automatic reconnection feature. MqttDispatcher provides this transparently
                mqttConnectOptions.isAutomaticReconnect = false
                // MqttRxClient requires clean sessions {@see MqttRxClient}
                mqttConnectOptions.isCleanSession = true
                // MQ credentials
                mqttConnectOptions.userName = MqConfiguration.USERNAME
                mqttConnectOptions.password = MqConfiguration.PASSWORD.toCharArray()
                mqttConnectOptions
            }

            /**
             * MQTT client
             */
            bind<IMqttAsyncClient>() with singleton {
                val androidContext = instance<Context>()
                val remoteSettings = instance<RemoteSettings>()
                val identity = instance<Identity>()

                MqttAsyncClient(
                        // Server URI
                        "tcp://${remoteSettings.host}:${remoteSettings.broker.nativePort}",
                        // Client ID
                        identity.key.value,
                        MemoryPersistence())
            }

            bind<MqttRxClient>() with singleton {
                val client = MqttRxClient(
                        parent = instance<IMqttAsyncClient>(),
                        connectOptions = instance<MqttConnectOptions>()
                )

                // TODO: dispatcher should support durable subscriptions
                client.statusEvent.subscribeBy(onNext = {
                    when {
                        it is MqttRxClient.Status.ConnectionComplete -> {
                            // Start listeners on connection
                            val mqConfig = instance<MqttConfiguration>()
                            mqConfig.mobileTopicListener.start()
                        }
                    }
                })

                client
            }

            bind<MqttDispatcher>() with singleton {
                val dispatcher = MqttDispatcher(
                        client = instance<MqttRxClient>(),
                        executorService = instance<ExecutorService>(),
                        persistence = MqttSqlitePersistence(
                                databaseFile = instance<Context>().getDatabasePath("mqtt.db"))
                )

                // Dispatcher will always auto-reconnect
                dispatcher.connect()

                // Wire connectivity
                instance<Connectivity>().networkProperty.subscribe {
                    when (it.value.state) {
                        NetworkInfo.State.CONNECTED -> dispatcher.connect()
                        else -> dispatcher.disconnect()
                    }
                }

                dispatcher
            }

            /**
             * MQTT configuration
             */
            bind<MqttConfiguration>() with singleton {
                MqttConfiguration(
                        context = MqttContext(
                                client = { instance<MqttDispatcher>() })
                )
            }

            bind<org.deku.leoz.config.MqttConfiguration>() with singleton {
                instance<MqttConfiguration>()
            }
        }
    }
}