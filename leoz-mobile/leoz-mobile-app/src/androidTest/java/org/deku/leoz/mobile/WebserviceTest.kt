package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.FeignRestClientConfiguration

/**
 * Created by n3 on 05/02/2017.
 */
abstract class WebserviceTest {
    companion object {
        init {
            Kodein.global.addImport(FeignRestClientConfiguration.module)

            val config: FeignRestClientConfiguration = Kodein.global.instance()
            config.sslValidation = false
            config.url = "http://192.168.0.140:13000/rs/api"
        }
    }

}