package org.deku.leoz.mobile.config

import android.content.Context
import ch.qos.logback.classic.LoggerContext
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.model.RemoteSettings
import org.deku.leoz.mobile.service.NotificationService
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.MqttListener
import sx.mq.mqtt.client


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

        val module = Kodein.Module {
            /**
             * MQTT connection options
             */
            bind<MqttConnectOptions>() with singleton {
                val mqttConnectOptions = MqttConnectOptions()
                mqttConnectOptions.isAutomaticReconnect = true
                mqttConnectOptions.isCleanSession = false
                mqttConnectOptions.userName = MqConfiguration.USERNAME
                mqttConnectOptions.password = MqConfiguration.PASSWORD.toCharArray()
                mqttConnectOptions
            }

            /**
             * MQTT client
             */
            bind<IMqttAsyncClient>() with eagerSingleton {
                val androidContext = instance<Context>()
                val remoteSettings = instance<RemoteSettings>()
                val identity = instance<Identity>()

                val mqttAndroidClient = MqttAndroidClient(
                        androidContext,
                        "tcp://${remoteSettings.host}:${remoteSettings.broker.nativePort}",
                        identity.key.value)

                mqttAndroidClient.setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String) {
                        log.info("Connected successfully")

                        if (reconnect) {
                            log.info("Reconnected successfully")
                        } else {
                            val appender = instance<LogMqAppender>()
                            appender.dispatcher.start()
                        }

                        val mqConfig = instance<MqttConfiguration>()
                        mqConfig.mobileTopicListener.start()
                    }

                    override fun connectionLost(cause: Throwable) {
                        log.info("Connection lost")
                    }

                    override fun messageArrived(topic: String, message: MqttMessage) {
                        log.trace("Incoming message")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken) {
                        log.trace("Delivery complete")
                    }
                })

                val mqttConnectOptions = instance<MqttConnectOptions>()

                try {
                    mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            log.info("Initial connection successful")

                            // Setup in disconnected in-memory buffer
                            val disconnectedBufferOptions = DisconnectedBufferOptions()
                            disconnectedBufferOptions.isBufferEnabled = true

                            // Persist disconnected messages using client persistence
                            disconnectedBufferOptions.isPersistBuffer = true

                            // In memory message buffer settings
                            disconnectedBufferOptions.bufferSize = 250
                            disconnectedBufferOptions.isDeleteOldestMessages = true

                            mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            log.error("Initial connection failed")
                        }
                    })
                } catch (ex: MqttException) {
                    ex.printStackTrace()
                }

                mqttAndroidClient
            }

            /**
             * MQTT configuration
             */
            bind<MqttConfiguration>() with singleton {
                MqttConfiguration(
                        context = MqttContext(
                                client = { instance<IMqttAsyncClient>() },
                                connectOptions = instance<MqttConnectOptions>()))

            }

            bind<org.deku.leoz.config.MqttConfiguration>() with singleton {
                instance<MqttConfiguration>()
            }
        }
    }

}