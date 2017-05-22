package sx.mq.mqtt

import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * MQTT persistence
 * Implmentations should be threadsafe
 * Created by masc on 20.05.17.
 */
interface IMqttPersistence {
    /**
     * Enqueue message
     */
    fun add(topicName: String, message: MqttMessage)

    /**
     * Retrieve messages in queued order
     * @param topicName Optional topic filter
     */
    fun get(topicName: String? = null): Observable<MqttPersistentMessage>

    /**
     * Remove specific message
     */
    fun remove(message: MqttPersistentMessage)
}

/**
 * MQTT in-memory persistence implementation
 */
class MqttInMemoryPersistence : IMqttPersistence {
    private var nextId: Int = 0
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val queue = ConcurrentLinkedQueue<MqttPersistentMessage>()

    override fun add(topicName: String, message: MqttMessage) {
        nextId++

        queue.add(message.toPersistentMessage(
                topicName = topicName,
                persistentId = nextId))
    }

    override fun get(topicName: String?): Observable<MqttPersistentMessage> {
        return Observable.fromIterable(
                if (topicName == null) queue else queue.filter { it.topicName == topicName }
        )
    }

    override fun remove(message: MqttPersistentMessage) {
        this.queue.remove(message)
    }
}

/**
 * Persistent MQTT message
 */
data class MqttPersistentMessage(
        val topicName: String,
        val payload: ByteArray,
        val qos: Int,
        val retained: Boolean,
        val messageId: Int,
        val persistentId: Int) {

    override fun hashCode(): Int {
        return this.persistentId
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MqttPersistentMessage

        if (persistentId != other.persistentId) return false

        return true
    }
}

/**
 * Convert paho MQTT messsage to gereric MQTT persistent message
 */
fun MqttMessage.toPersistentMessage(topicName: String, persistentId: Int): MqttPersistentMessage {
    return MqttPersistentMessage(
            topicName = topicName,
            payload = this.payload,
            qos = this.qos,
            retained = this.isRetained,
            messageId = this.id,
            persistentId = persistentId)
}

/**
 * Convert persistent message to paho MQTT message
 */
fun MqttPersistentMessage.toMqttMessage(): MqttMessage {
    val m = MqttMessage()
    m.payload = this.payload
    m.qos = this.qos
    m.isRetained = this.retained
    m.id = this.messageId
    return m
}