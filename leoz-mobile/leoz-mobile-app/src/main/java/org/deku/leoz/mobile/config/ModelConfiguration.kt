package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.mobile.model.Job
import org.deku.leoz.mobile.model.Login
import sx.concurrent.task.CompositeExecutorService
import java.util.concurrent.*

/**
 * Model configuration
 * Created by masc on 10/02/2017.
 */
class ModelConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Login>() with singleton {
                Login()
            }

            bind<Job>() with eagerSingleton {
                Job()
            }
        }
    }
}