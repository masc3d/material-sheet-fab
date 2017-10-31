package org.deku.leoz.mobile.config

import android.content.Context
import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.settings.RemoteSettings
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.mq.MqttListeners
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import sx.android.Connectivity
import sx.android.mqtt.MqttSqlitePersistence
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.MqttDispatcher
import sx.mq.mqtt.MqttRxClient
import java.util.concurrent.ExecutorService


/**
 * Mobile MQTT configuration
 * Created by masc on 10.05.17.
 */
class MqttConfiguration {

    companion object {
        private val log = LoggerFactory.getLogger(MqttConfiguration::class.java)

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
                val remoteSettings = instance<RemoteSettings>()
                val identity = instance<Identity>()

                MqttAsyncClient(
                        // Server URI
                        "tcp://${remoteSettings.host}:${remoteSettings.broker.nativePort}",
                        // Client ID
                        identity.uid.value,
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
                            instance<MqttListeners>().mobile.topic.start()
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

                // Wire connectivity
                instance<Connectivity>().networkProperty.subscribe {
                    when (it.value.state) {
                        NetworkInfo.State.CONNECTED -> dispatcher.connect()
                        else -> dispatcher.disconnect()
                    }
                }

                dispatcher
            }

            bind<MqttContext>() with singleton {
                MqttContext(
                        // Using the dispatcher as a client proxy for transparent reocnnection and persistence
                        client = { instance<MqttDispatcher>() }
                )
            }

            bind<MqttEndpoints>() with singleton {
                MqttEndpoints(
                        context = instance<MqttContext>()
                )
            }

            bind<MqttListeners>() with singleton {
                MqttListeners(
                        endpoints = instance<MqttEndpoints>()
                )
            }
        }
    }
}