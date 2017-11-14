package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.RestClientFactory

/**
 * Created by masc on 05/02/2017.
 */
abstract class WebserviceTest {
    companion object {
        init {
            val config: RestClientFactory = Kodein.global.instance()
            config.host = "192.168.0.28"
            config.https = false
        }
    }

}