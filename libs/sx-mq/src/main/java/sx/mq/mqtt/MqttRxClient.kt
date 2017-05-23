package sx.mq.mqtt

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import sx.rx.toHotCache
import sx.rx.toHotReplay

/**
 * MQTT reactive client interface
 */
interface IMqttRxClient {
    /**
     * Publish to topic
     */
    fun publish(topicName: String, message: MqttMessage): Completable

    /**
     * Subscribe to topic
     */
    fun subscribe(topicName: String, qos: Int): Observable<MqttMessage>

    /**
     * Unsubscribe from topic
     */
    fun unsubscribe(topicName: String): Completable

    /**
     * Establish connection
     */
    fun connect(): Completable

    /**
     * Disconnect client
     */
    fun disconnect(): Completable
}

/**
 * MQTT rx client providing a more consistent and reactive way of interacting wiht a paho MQTT client
 * Created by masc on 19.05.17.
 */
class MqttRxClient(
        private val parent: IMqttAsyncClient,
        private val connectOptions: MqttConnectOptions) : IMqttRxClient {

    abstract class Status {
        data class ConnectionComplete(val reconnect: Boolean, val serverURI: String) : MqttRxClient.Status()
        data class ConnectionLost(val cause: Throwable?) : MqttRxClient.Status()
        data class MessageArrived(val topic: String, val message: MqttMessage) : MqttRxClient.Status()
        data class DeliveryComplete(val token: IMqttDeliveryToken?) : MqttRxClient.Status()
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        if (connectOptions.isCleanSession == false) {
            // When cleanSession is false, paho's async function are not determinstic, eg.
            // publish may invoke onFailure callback mutliple times as it attempts retries.
            // All RX operations need to either complete or terminate with error (once).
            // This is basically a design flaw in paho, as a clean session should not necessarily
            // imply that async calls become non-determinstic due to internal retries.
            throw IllegalStateException("Clean session not set in connection options. Unclean sessions are not compatible with MqttRxClient")
        }
    }

    /**
     * Connection status event
     */
    val statusEvent by lazy { statusSubject.hide() }
    private val statusSubject = PublishSubject.create<MqttRxClient.Status>().toSerialized()

    init {
        // Wire paho's callback to rx subject
        this.parent.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                statusSubject.onNext(MqttRxClient.Status.ConnectionComplete(reconnect, serverURI))
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                statusSubject.onNext(MqttRxClient.Status.MessageArrived(topic, message))
            }

            override fun connectionLost(cause: Throwable?) {
                statusSubject.onNext(MqttRxClient.Status.ConnectionLost(cause))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                statusSubject.onNext(MqttRxClient.Status.DeliveryComplete(token))
            }
        })
    }

    /**
     * Publish to topic
     * @param topicName Topic name
     * @param message Message to publish
     */
    override fun publish(topicName: String, message: MqttMessage): Completable {
        return Completable.create {
            try {
                this.parent.publish(
                        topicName,
                        message,
                        null,
                        object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                it.onComplete()
                            }

                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                it.onError(exception ?: MqttException(null))
                            }
                        }
                )
            } catch(e: Throwable) {
                it.onError(e)
            }
        }
                .toHotCache()
    }

    /**
     * Subscribe to topic
     * @param topicName Topic name
     * @param qos Message QOS
     */
    override fun subscribe(topicName: String, qos: Int): Observable<MqttMessage> {
        return Observable.create<MqttMessage> { subscriber ->
            try {
                this.parent.subscribe(
                        topicName,
                        qos,
                        null,
                        object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                            }

                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                subscriber.onError(exception ?: MqttException(null))
                            }

                        },
                        object : IMqttMessageListener {
                            override fun messageArrived(topic: String, message: MqttMessage) {
                                subscriber.onNext(message)
                            }
                        })
            } catch(e: Throwable) {
                subscriber.onError(e)
            }
        }
                .toHotReplay()
    }

    /**
     * Unsubscribe
     * @param topicName Topic name to unsubscribe from
     */
    override fun unsubscribe(topicName: String): Completable {
        return Completable.create {
            try {
                this.parent.unsubscribe(topicName, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        it.onComplete()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        it.onError(exception ?: MqttException(null))
                    }

                })
            } catch(e: Throwable) {
                it.onError(e)
            }
        }
                .toHotCache()
    }


    /**
     * Establish connection
     * @param options Connection options
     */
    override fun connect(): Completable {
        return Completable.create {
            try {
                if (!this.parent.isConnected) {
                    this.parent.connect(this.connectOptions,
                            null,
                            object : IMqttActionListener {
                                override fun onSuccess(asyncActionToken: IMqttToken?) {
                                    it.onComplete()
                                }

                                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                    it.onError(exception ?: MqttException(null))
                                }
                            })
                } else {
                    it.onComplete()
                }
            } catch(e: Throwable) {
                it.onError(e)
            }
        }
                .toHotCache()
    }

    /**
     * Disconnect
     */
    override fun disconnect(): Completable {
        return Completable.create {
            try {
                this.parent.disconnect(
                        null,
                        object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                it.onComplete()
                            }

                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                it.onError(exception ?: MqttException(null))
                            }
                        })
            } catch(e: Throwable) {
                it.onError(e)
            }
        }
                .toHotCache()
    }

    /**
     * Is currently connected
     */
    val isConnected: Boolean
        get() = this.parent.isConnected
}
