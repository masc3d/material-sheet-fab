package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.mobile.model.RemoteSettings
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener


/**
 * Mobile MQTT configuration
 * Created by n3 on 10.05.17.
 */
class MqttConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(MqttConfiguration::class.java)

        val module = Kodein.Module {
        }
    }

}