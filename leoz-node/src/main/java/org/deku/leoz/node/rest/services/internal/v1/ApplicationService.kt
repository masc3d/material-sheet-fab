package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.node.App
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import sx.rs.ApiKey
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.util.jar.Manifest
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 09.10.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/application")
@Produces(MediaType.APPLICATION_JSON)
class ApplicationService : org.deku.leoz.rest.services.internal.v1.ApplicationService {
    override fun getVersion(): ApplicationVersion {
        return ApplicationVersion(
                App.instance().jarManifest.ImplementationName,
                App.instance().jarManifest.ImplementationVersion)
    }
}