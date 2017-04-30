package org.deku.leoz.node.service.internal

import org.deku.leoz.node.Application
import org.deku.leoz.service.entity.internal.update.UpdateInfo
import org.deku.leoz.service.internal.BundleService
import org.deku.leoz.service.internal.BundleServiceV2
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
open class BundleServiceV2 : BundleServiceV2 {
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