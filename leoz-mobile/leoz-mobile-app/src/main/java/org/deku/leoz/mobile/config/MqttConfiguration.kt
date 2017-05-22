package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.model.RemoteSettings
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import sx.android.mqtt.MqttClientPersistenceSQLite
import sx.mq.mqtt.*
import sx.rx.retryWith
import sx.rx.subscribeOn
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit


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

        /**
         * Extension to enable paho's disconnected offline buffering
         */
        fun MqttAndroidClient.enableDisconnectedBuffer() {
            // Setup in disconnected in-memory buffer
            val disconnectedBufferOptions = DisconnectedBufferOptions()
            disconnectedBufferOptions.isBufferEnabled = false

            // Persist disconnected messages using client persistence
            disconnectedBufferOptions.isPersistBuffer = false

            // In memory message buffer settings
            disconnectedBufferOptions.bufferSize = 250
            disconnectedBufferOptions.isDeleteOldestMessages = false

            this.setBufferOpts(disconnectedBufferOptions)
        }

        val module = Kodein.Module {
            /**
             * MQTT connection options
             */
            bind<MqttConnectOptions>() with singleton {
                val mqttConnectOptions = MqttConnectOptions()
                mqttConnectOptions.isAutomaticReconnect = false
                mqttConnectOptions.isCleanSession = true
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

                MqttAndroidClient(
                        androidContext,
                        // Server URI
                        "tcp://${remoteSettings.host}:${remoteSettings.broker.nativePort}",
                        // Client ID
                        identity.key.value)
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
                        persistence = MqttInMemoryPersistence()
                )

                // Dispatcher will always auto-reconnect
                dispatcher.connect()

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