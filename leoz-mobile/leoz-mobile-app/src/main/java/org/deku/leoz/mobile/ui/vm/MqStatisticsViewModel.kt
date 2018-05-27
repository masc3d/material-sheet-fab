package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.mq.mqtt.MqttDispatcher

/**
 * MQ statistics view model
 * Created by masc on 08.09.17.
 */
class MqStatisticsViewModel(
        val mqttDispatcher: MqttDispatcher,
        val mqttEndpoints: MqttEndpoints
) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val mainCountObservable = this.mqttDispatcher.statisticsUpdateEvent.map {
        it.get(mqttEndpoints.central.main.topicName) ?: 0
    }

    /** Value from 0..1000 indicating progress */
    val mainCount: ObservableField<String> by lazy { this.mainCountObservable.map { it.toString() }.toField() }

    /** Progress visibility */
    val isVisible: ObservableField<Boolean> by lazy { this.mainCountObservable.map { it > 0 }.toField() }
}
