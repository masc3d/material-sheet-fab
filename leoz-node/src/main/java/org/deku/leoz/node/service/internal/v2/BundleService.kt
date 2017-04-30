package org.deku.leoz.node.service.internal.v2

import org.deku.leoz.node.Application
import org.deku.leoz.service.entity.internal.v1.update.UpdateInfo
import org.deku.leoz.service.internal.v1.BundleService
import org.springframework.context.annotation.Profile
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * BundleService implementation
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Profile(Application.PROFILE_CLIENT_NODE)
@Path("internal/v2/bundle")
open class BundleService : org.deku.leoz.service.internal.v2.BundleService {
    @Inject
    protected lateinit var bundleService1: BundleService

    override fun info(bundleName: String, versionAlias: String?, nodeKey: String?): UpdateInfo {
        return bundleService1.info(
                bundleName = bundleName,
                versionAlias = versionAlias,
                nodeKey = nodeKey)
    }

    override fun download(bundleName: String, version: String): Response {
        return bundleService1.download(
                bundleName = bundleName,
                version = version
        )
    }

    override fun clean() {
        bundleService1.cleanRepository()
    }
}