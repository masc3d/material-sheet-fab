package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
import org.deku.leoz.mobile.Storage
import sx.android.Connectivity
import sx.android.Device
import java.io.File
import java.io.FileWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.util.concurrent.ExecutorService

/**
 * Created by n3 on 26/02/2017.
 */
class DeviceConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Device>() with singleton {
                Device(context = instance())
            }

            bind<Identity>() with singleton {
                val device = instance<Device>()
                val storage = instance<Storage>()
                val identity = MobileIdentityFactory(
                        serial = device.serial,
                        imei = device.imei
                ).create()

                // Use this for CT50 devices only - File needed to display the shortUuid in the MDM
                if (device.model.name == "CT50") {
                    val file = File(storage.externalDir, "identity.xml")
                    if (!file.exists()) {
                        val writer = FileWriter(file)
                        writer.append("<device-id>${identity.shortUid}</device-id>")
                        writer.flush()
                        writer.close()
                    }
                }

                identity
            }

            // Intiial connectivity state updates may take a while to arrive, thus binding eagerly
            bind<Connectivity>() with eagerSingleton {
                Connectivity(context = instance<Context>())
            }
        }
    }
}